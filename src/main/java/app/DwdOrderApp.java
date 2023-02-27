package app;

import bean.OrderDetail;
import bean.OrderInfo;
import bean.OrderWide;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;
import redis.clients.jedis.Jedis;
import scala.Tuple2;
import util.MyEsUtils;
import util.MyKafkaUtils;
import util.MyOffsetsUtils;
import util.MyRedisUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * @author Akang
 * @create 2022-12-15 17:55
 */
public class DwdOrderApp {
    public static void main(String[] args) throws Exception {
        // 1. 准备实时环境
        SparkConf conf = new SparkConf().setAppName("DWD_Order_App").setMaster("local[*]");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(2));
        // 2.从redis 中读取offset
        String orderInfoTopicName = "DWD_ORDER_INFO_I";
        String orderInfoGoupID = "DWD_ORDER_INFO_GROUP";
        String orderInfoOffsetKey = "offset:" + orderInfoTopicName + ":" + orderInfoGoupID;
        Map<TopicPartition, Long> orderInfoOffsets = MyOffsetsUtils.readOffset(orderInfoTopicName, orderInfoOffsetKey);

        String orderDetailTopicName = "DWD_ORDER_DETAIL_I";
        String orderDetailGroupID = "DWD_ORDER_DETAIL_GROUP";
        String orderDetailOffsetKey = "offset:" + orderDetailTopicName + ":" + orderDetailGroupID;
        Map<TopicPartition, Long> orderDetailOffsets = MyOffsetsUtils.readOffset(orderDetailTopicName, orderDetailOffsetKey);

        // 3.消费kafka 数据
        JavaInputDStream<ConsumerRecord<String, String>> orderInfoKafkaDStream = null;
        if (orderInfoOffsets != null && !orderInfoOffsets.isEmpty()) {
            orderInfoKafkaDStream = MyKafkaUtils.getKafkaDStream(jssc, orderInfoTopicName, orderInfoGoupID, orderInfoOffsets);
        } else {
            orderInfoKafkaDStream = MyKafkaUtils.getKafkaDStream(jssc, orderInfoTopicName, orderInfoGoupID);
        }

        JavaInputDStream<ConsumerRecord<String, String>> orderDetailKafkaDStream = null;
        if (orderDetailOffsets != null && !orderDetailOffsets.isEmpty()) {
            orderDetailKafkaDStream = MyKafkaUtils.getKafkaDStream(jssc, orderDetailTopicName, orderDetailGroupID, orderDetailOffsets);
        } else {
            orderDetailKafkaDStream = MyKafkaUtils.getKafkaDStream(jssc, orderDetailTopicName, orderDetailGroupID);
        }

        // 4.数据转换
        JavaDStream<OrderInfo> orderInfoDStream = orderInfoKafkaDStream.mapPartitions(rddIterator -> {
            List<OrderInfo> orderInfos = new ArrayList<>();
            while (rddIterator.hasNext()) {
                String value = rddIterator.next().value();
                OrderInfo orderInfo = JSON.parseObject(value, OrderInfo.class);
                orderInfos.add(orderInfo);
            }
            return orderInfos.iterator();
        });

        JavaDStream<OrderDetail> orderDetailDStream = orderDetailKafkaDStream.mapPartitions(rddIterator -> {
            List<OrderDetail> orderDetails = new ArrayList<>();
            while (rddIterator.hasNext()) {
                String value = rddIterator.next().value();
                OrderDetail orderDetail = JSON.parseObject(value, OrderDetail.class);
                orderDetails.add(orderDetail);
            }
            return orderDetails.iterator();
        });

        // 5.进行维度关联
        JavaDStream<OrderInfo> orderInfoDimDStream = orderInfoDStream.mapPartitions(dsIterator -> {
            List<OrderInfo> orderInfos = new ArrayList<>();
            Jedis jedis = MyRedisUtils.getJedisFromPool();
            while (dsIterator.hasNext()) {
                OrderInfo next = dsIterator.next();
                // 1. 补充用户信息
                Long id = next.getUser_id();
                String redisUidkey = "DIM:USER_INFO:" + id;
                String value = jedis.get(redisUidkey);
                JSONObject jsonStr = JSON.parseObject(value);
                if (jsonStr!=null){
                    String gender = jsonStr.getString("gender");
                    String birthday = jsonStr.getString("birthday");
                    //年龄换算
                    LocalDate bitrhLocalDate = LocalDate.parse(birthday);
                    LocalDate now = LocalDate.now();
                    int ageOld = Period.between(bitrhLocalDate, now).getYears();
                    //补充用户信息
                    next.setUser_age(ageOld);
                    next.setUser_gender(gender);
                }


                // 2.补充地区维度
                Long province_id = next.getProvince_id();
                String redisProvinceKey = "DIM:BASE_PROVINCE:" + province_id;
                String provinceValue = jedis.get(redisProvinceKey);
                JSONObject provinceJsonObj = JSON.parseObject(provinceValue);
                if (provinceJsonObj !=null){
                    String name = provinceJsonObj.getString("name");
                    String area_code = provinceJsonObj.getString("area_code");
                    String iso_3166_2 = provinceJsonObj.getString("iso_3166_2");
                    String iso_code = provinceJsonObj.getString("iso_code");
                    //补充到对象中去
                    next.setProvince_name(name);
                    next.setProvince_area_code(area_code);
                    next.setProvince_3166_2_code(iso_3166_2);
                    next.setProvince_iso_code(iso_code);
                }

                //处理日期字段
                String create_date = next.getCreate_time();
                String[] dateSplits = create_date.split(" ");
                String createDate = dateSplits[0];
                String createHr = dateSplits[1].split(":")[0];
                next.setCreate_date(createDate);
                next.setCreate_hour(createHr);

                orderInfos.add(next);
            }
            return orderInfos.iterator();
        });

        // 6.进行双流 join
        // 从数据库层面： order_info 表中的数据 和 order_detail表中的数据一定能关联成功.
        // 从流处理层面:  order_info 和  order_detail是两个流， 流的join只能是同一个批次的数据才能进行join
        // 如果两个表的数据进入到不同批次中， 就会join不成功.
        // 数据延迟导致的数据没有进入到同一个批次，在实时处理中是正常现象. 我们可以接收因为延迟导致最终的结果延迟.
        // 我们不能接收因为延迟导致的数据丢失.
        JavaPairDStream<Long, OrderInfo> orderInfoKVDStream = orderInfoDimDStream.mapPartitionsToPair(
                new PairFlatMapFunction<Iterator<OrderInfo>, Long, OrderInfo>() {
                    @Override
                    public Iterator<Tuple2<Long, OrderInfo>> call(Iterator<OrderInfo> orderInfoIterator) throws Exception {
                        List<Tuple2<Long, OrderInfo>> orderInfoList = new ArrayList<>();
                        while (orderInfoIterator.hasNext()) {
                            OrderInfo orderInfo = orderInfoIterator.next();
                            orderInfoList.add(new Tuple2<>(orderInfo.getId(), orderInfo));
                        }
                        return orderInfoList.iterator();
                    }
                });
        JavaPairDStream<Long, OrderDetail> orderDetailKVDStream = orderDetailDStream.mapPartitionsToPair(
                new PairFlatMapFunction<Iterator<OrderDetail>, Long, OrderDetail>() {
                    @Override
                    public Iterator<Tuple2<Long, OrderDetail>> call(Iterator<OrderDetail> orderDetailIterator) throws Exception {
                        List<Tuple2<Long, OrderDetail>> orderDetailList = new ArrayList<>();
                        while (orderDetailIterator.hasNext()) {
                            OrderDetail orderDetail = orderDetailIterator.next();
                            orderDetailList.add(new Tuple2<>(orderDetail.getOrder_id(), orderDetail));
                        }
                        return orderDetailList.iterator();
                    }
                });
        // 解决:
        //  1. 扩大采集周期 ， 治标不治本
        //  2. 使用窗口,治标不治本 , 还要考虑数据去重 、 Spark状态的缺点
        //  3. 首先使用fullOuterJoin,保证join成功或者没有成功的数据都出现到结果中.
        //     让双方都多两步操作, 到缓存中找对的人， 把自己写到缓存中
        JavaPairDStream<Long, Tuple2<Optional<OrderInfo>, Optional<OrderDetail>>> orderJoinDStream
                = orderInfoKVDStream.fullOuterJoin(orderDetailKVDStream);

        JavaDStream<OrderWide> orderWideDStream = orderJoinDStream.mapPartitions(
                new FlatMapFunction<Iterator<Tuple2<Long, Tuple2<Optional<OrderInfo>, Optional<OrderDetail>>>>, OrderWide>() {
                    @Override
                    public Iterator<OrderWide> call(
                            Iterator<Tuple2<Long, Tuple2<Optional<OrderInfo>, Optional<OrderDetail>>>> tIterator) throws Exception {
                        List<OrderWide> orderWideList = new ArrayList<>();
                        Jedis jedis = MyRedisUtils.getJedisFromPool();
                        while (tIterator.hasNext()) {
                            Tuple2<Long, Tuple2<Optional<OrderInfo>, Optional<OrderDetail>>> next = tIterator.next();
                            Optional<OrderInfo> orderInfoOptional = next._2._1;
                            Optional<OrderDetail> orderDetailOptional = next._2._2;
                            //orderInfo有， orderDetail有
                            if (orderInfoOptional.isPresent() && orderDetailOptional.isPresent()) {
                                OrderInfo orderInfo = orderInfoOptional.get();
                                OrderDetail orderDetail = orderDetailOptional.get();
                                OrderWide orderWide = new OrderWide(
                                        orderDetail.getId(),
                                        orderInfo.getId(),
                                        orderDetail.getSku_id(),
                                        orderDetail.getOrder_price(),
                                        orderDetail.getSku_num(),
                                        orderDetail.getSku_name(),
                                        orderDetail.getSplit_total_amount(),
                                        orderDetail.getSplit_activity_amount(),
                                        orderDetail.getSplit_coupon_amount(),
                                        orderInfo.getProvince_id(),
                                        orderInfo.getOrder_status(),
                                        orderInfo.getUser_id(),
                                        orderInfo.getTotal_amount(),
                                        orderInfo.getActivity_reduce_amount(),
                                        orderInfo.getCoupon_reduce_amount(),
                                        orderInfo.getOriginal_total_amount(),
                                        orderInfo.getFeight_fee(),
                                        orderInfo.getFeight_fee_reduce(),
                                        orderInfo.getExpire_time(),
                                        orderInfo.getRefundable_time(),
                                        orderInfo.getCreate_time(),
                                        orderInfo.getOperate_time(),
                                        orderInfo.getCreate_date(),
                                        orderInfo.getCreate_hour(),
                                        orderInfo.getProvince_name(),
                                        orderInfo.getProvince_area_code(),
                                        orderInfo.getProvince_3166_2_code(),
                                        orderInfo.getProvince_iso_code(),
                                        orderInfo.getUser_age(),
                                        orderInfo.getUser_gender()
                                );
                                orderWideList.add(orderWide);
                            }

                            //orderInfo有，orderDetail没有
                            // orderInfo写缓存
                            // 类型:  string
                            // key :   ORDERJOIN:ORDER_INFO:ID
                            // value :  json
                            // 写入API:  set
                            // 读取API:  get
                            // 是否过期: 24小时
                            if (orderInfoOptional.isPresent() && !orderDetailOptional.isPresent()) {
                                OrderInfo orderInfo = orderInfoOptional.get();
                                // 1. orderInfo写缓存
                                String redisOrderInfoKey = "ORDERJOIN:ORDER_INFO:" + orderInfo.getId();
                                jedis.setex(redisOrderInfoKey, 24 * 3600, JSON.toJSONString(
                                        orderInfo, new SerializeConfig(true)));

                                // 2. orderInfo读缓存
                                String redisOrderDetailKey = "ORDERJOIN:ORDER_DETAIL:" + orderInfo.getId();
                                Set<String> orderDetails = jedis.smembers(redisOrderDetailKey);
                                if (orderDetails != null && orderDetails.size() > 0) {
                                    for (String orderDetailJson : orderDetails) {
                                        OrderDetail orderDetail = JSON.parseObject(orderDetailJson, OrderDetail.class);
                                        OrderWide orderWide = new OrderWide(
                                                orderDetail.getId(),
                                                orderInfo.getId(),
                                                orderDetail.getSku_id(),
                                                orderDetail.getOrder_price(),
                                                orderDetail.getSku_num(),
                                                orderDetail.getSku_name(),
                                                orderDetail.getSplit_total_amount(),
                                                orderDetail.getSplit_activity_amount(),
                                                orderDetail.getSplit_coupon_amount(),
                                                orderInfo.getProvince_id(),
                                                orderInfo.getOrder_status(),
                                                orderInfo.getUser_id(),
                                                orderInfo.getTotal_amount(),
                                                orderInfo.getActivity_reduce_amount(),
                                                orderInfo.getCoupon_reduce_amount(),
                                                orderInfo.getOriginal_total_amount(),
                                                orderInfo.getFeight_fee(),
                                                orderInfo.getFeight_fee_reduce(),
                                                orderInfo.getExpire_time(),
                                                orderInfo.getRefundable_time(),
                                                orderInfo.getCreate_time(),
                                                orderInfo.getOperate_time(),
                                                orderInfo.getCreate_date(),
                                                orderInfo.getCreate_hour(),
                                                orderInfo.getProvince_name(),
                                                orderInfo.getProvince_area_code(),
                                                orderInfo.getProvince_3166_2_code(),
                                                orderInfo.getProvince_iso_code(),
                                                orderInfo.getUser_age(),
                                                orderInfo.getUser_gender());
                                        orderWideList.add(orderWide);
                                    }
                                }
                            }

                            //orderInfo没有， orderDetail有
                            if (!orderInfoOptional.isPresent() && orderDetailOptional.isPresent()) {
                                OrderDetail orderDetail = orderDetailOptional.get();
                                // 1. ordrDetail读缓存
                                String redisOrderInfoKey = "ORDERJOIN:ORDER_INFO:" + orderDetail.getOrder_id();
                                String orderInfoJson = jedis.get(redisOrderInfoKey);
                                if (orderInfoJson != null && orderInfoJson.length() > 0) {
                                    OrderInfo orderInfo = JSON.parseObject(orderInfoJson, OrderInfo.class);
                                    OrderWide orderWide = new OrderWide(
                                            orderDetail.getId(),
                                            orderInfo.getId(),
                                            orderDetail.getSku_id(),
                                            orderDetail.getOrder_price(),
                                            orderDetail.getSku_num(),
                                            orderDetail.getSku_name(),
                                            orderDetail.getSplit_total_amount(),
                                            orderDetail.getSplit_activity_amount(),
                                            orderDetail.getSplit_coupon_amount(),
                                            orderInfo.getProvince_id(),
                                            orderInfo.getOrder_status(),
                                            orderInfo.getUser_id(),
                                            orderInfo.getTotal_amount(),
                                            orderInfo.getActivity_reduce_amount(),
                                            orderInfo.getCoupon_reduce_amount(),
                                            orderInfo.getOriginal_total_amount(),
                                            orderInfo.getFeight_fee(),
                                            orderInfo.getFeight_fee_reduce(),
                                            orderInfo.getExpire_time(),
                                            orderInfo.getRefundable_time(),
                                            orderInfo.getCreate_time(),
                                            orderInfo.getOperate_time(),
                                            orderInfo.getCreate_date(),
                                            orderInfo.getCreate_hour(),
                                            orderInfo.getProvince_name(),
                                            orderInfo.getProvince_area_code(),
                                            orderInfo.getProvince_3166_2_code(),
                                            orderInfo.getProvince_iso_code(),
                                            orderInfo.getUser_age(),
                                            orderInfo.getUser_gender());
                                    orderWideList.add(orderWide);
                                } else {
                                    //写缓存
                                    // 类型:   set
                                    // key :   ORDERJOIN:ORDER_DETAIL:ORDER_ID
                                    // value :  json, json ....
                                    // 写入API: sadd
                                    // 读取API: smembers
                                    // 是否过期: 24小时

                                    String redisOrderDetailKey = "ORDERJOIN:ORDER_DETAIL:" + orderDetail.getOrder_id();
                                    jedis.sadd(redisOrderDetailKey, JSON.toJSONString(orderDetail, new SerializeConfig(true)));
                                    jedis.expire(redisOrderDetailKey, 24 * 3600);
                                }
                            }
                        }
                        jedis.close();
                        return orderWideList.iterator();
                    }
                });
        orderWideDStream.print(5);

        // 7.发送到 es
        orderWideDStream.foreachRDD(dstream -> {
            dstream.foreachPartition(dstreamIterator -> {
                while (dstreamIterator.hasNext()){
                    OrderWide orderWide = dstreamIterator.next();
                    String id = orderWide.getDetail_id().toString();
                    String value = JSON.toJSONString(orderWide,new SerializeConfig(true));
                    String create_date = orderWide.getCreate_date();
                    String indexName = "gmall_order_wide_" + create_date ;
                    MyEsUtils.bulkSave(indexName,id,value);
                }
            });
        });

        // 8.提交 offset
        orderInfoKafkaDStream.foreachRDD(rdd -> {
            OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
            MyOffsetsUtils.saveOffset(orderInfoOffsetKey, offsetRanges);
        });

        orderDetailKafkaDStream.foreachRDD(rdd -> {
            OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
            MyOffsetsUtils.saveOffset(orderDetailOffsetKey, offsetRanges);
        });

        // 9.程序执行
        jssc.start();
        jssc.awaitTermination();
    }
}
