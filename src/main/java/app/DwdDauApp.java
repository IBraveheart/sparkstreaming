package app;


import bean.DauInfo;
import bean.PageLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import util.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * @author Akang
 * @create 2022-11-29 11:25
 */
public class DwdDauApp {
    private static String redisOffsetKey = "offset:DWD_PAGE_LOG_TOPIC:DWD_DAU_GROUP";

    public static void main(String[] args) throws Exception {
        String topicName = "DWD_PAGE_LOG_TOPIC";
        String groupId = "DWD_DAU_GROUP";
        // 状态还原
        revertState();

        // 准备事实执行环境
        SparkConf sc = new SparkConf().setMaster("local[*]").setAppName("dwd_log_run");
        JavaStreamingContext jssc = new JavaStreamingContext(sc, Durations.seconds(2));

        // 使用指定的 offset 进行消费(offset保存在redis中)
        Map<TopicPartition, Long> offsets = MyOffsetsUtils.readOffset(topicName, redisOffsetKey);

        // 3.消费kafka 数据
        JavaInputDStream<ConsumerRecord<String, String>> DStream = null;
        if (offsets != null && !offsets.isEmpty()) {
            DStream = MyKafkaUtils.getKafkaDStream(
                    jssc,
                    topicName,
                    groupId,
                    offsets);
        } else {
            DStream = MyKafkaUtils.getKafkaDStream(
                    jssc,
                    topicName,
                    groupId);
        }

        // 数据处理，数据转换
        JavaDStream<PageLog> pageLog = DStream.map(new Function<ConsumerRecord<String, String>, PageLog>() {
            @Override
            public PageLog call(ConsumerRecord<String, String> records) throws Exception {
                String s = records.value();
                PageLog pglog = JSON.parseObject(s, PageLog.class);
                return pglog;
            }
        });
        pageLog.cache();
        pageLog.print(10);

        // 去重处理
        JavaDStream<PageLog> filter = pageLog.filter(rdd -> {
            return rdd.getLast_page_id() == null;
        });
        filter.foreachRDD(rdd -> System.out.println("自我审查后: " + rdd.count()));

        // 第三方redis去重

        JavaDStream<PageLog> redisFilterDStream = filter.mapPartitions(new FlatMapFunction<Iterator<PageLog>, PageLog>() {
            @Override
            public Iterator<PageLog> call(Iterator<PageLog> pageLogIterator) throws Exception {
                List<PageLog> results = new ArrayList<>();
                Jedis jedis = MyRedisUtils.getJedisFromPool();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                while (pageLogIterator.hasNext()) {
                    PageLog p = pageLogIterator.next();
                    String mid = p.getMid();
                    Long ts = p.getTs();
                    Date date = new Date(ts);
                    String format = sdf.format(date);
                    String redisKey = "DAU:" + format;

                    Long isNew = jedis.sadd(redisKey, mid);
                    if (isNew == 1L) {
                        results.add(p);
                    }
                }
                jedis.close();
                return results.iterator();
            }
        });
        redisFilterDStream.foreachRDD(rdd->{
            System.out.println("第三方(redis)审查后的结果:" + rdd.count());
        });

        // 维度关联
        JavaDStream<DauInfo> dauinfoDStream = redisFilterDStream.mapPartitions(new FlatMapFunction<Iterator<PageLog>, DauInfo>() {
            @Override
            public Iterator<DauInfo> call(Iterator<PageLog> pageLogIterator) throws Exception {
                List<DauInfo> dauinfos = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Jedis jedis = MyRedisUtils.getJedisFromPool();
                while (pageLogIterator.hasNext()) {
                    DauInfo dauInfo = new DauInfo();
                    PageLog next = pageLogIterator.next();

                    // 页面基本信息
                    dauInfo.setMid(next.getMid());
                    dauInfo.setUser_id(next.getUser_id());
                    dauInfo.setProvince_id(next.getProvince_id());
                    dauInfo.setChannel(next.getChannel());
                    dauInfo.setIs_new(next.getIs_new());
                    dauInfo.setModel(next.getModel());
                    dauInfo.setOperate_system(next.getOperate_system());
                    dauInfo.setVersion_code(next.getVersion_code());
                    dauInfo.setBrand(next.getBrand());
                    dauInfo.setPage_id(next.getPage_id());
                    dauInfo.setPage_item(next.getPage_item());
                    dauInfo.setPage_item_type(next.getPage_item_type());
                    dauInfo.setSourceType(next.getSourceType());
                    dauInfo.setDuring_timeLong(next.getDuring_time());
                    dauInfo.setTs(next.getTs());

                    // 补充维度
                    //用户信息
                    String user_id = next.getUser_id();
                    String redisUidKey = "DIM:USER_INFO:" + user_id;
                    String s3 = jedis.get(redisUidKey);
                    JSONObject jsonObject = JSON.parseObject(s3);
                    if (jsonObject!=null){
                        // 提取性别
                        String gender = jsonObject.getString("gender");
                        // 提取生日
                        String birthday = jsonObject.getString("birthday");
                        // 换算年龄
                        LocalDate birthdayLd = LocalDate.parse(birthday);
                        LocalDate now = LocalDate.now();
                        Period period = Period.between(birthdayLd, now);
                        int age = period.getYears();
                        dauInfo.setUser_gender(gender);
                        dauInfo.setUser_age(Integer.toString(age));
                    }

                    // 地区信息
                    String province_id = next.getProvince_id();
                    String redisProvinceKey = "DIM:BASE_PROVINCE:" + province_id;
                    String s4 = jedis.get(redisProvinceKey);
                    JSONObject provinceJson = JSON.parseObject(s4);
                    if (provinceJson!=null){
                        String provinceName = provinceJson.getString("name");
                        String provinceIsoCode = provinceJson.getString("iso_code");
                        String province3166 = provinceJson.getString("iso_3166_2");
                        String provinceAreaCode = provinceJson.getString("area_code");

                        dauInfo.setProvince_name(provinceName);
                        dauInfo.setProvince_iso_code(provinceIsoCode);
                        dauInfo.setProvince_3166_2(province3166);
                        dauInfo.setUser_age(provinceAreaCode);
                    }

                    // 日期字段处理
                    Date date = new Date(next.getTs());
                    String format = sdf.format(date);
                    String[] s = format.split(" ");
                    String s1 = s[0];
                    String s2 = s[1].split(":")[0];

                    dauInfo.setDt(s1);
                    dauInfo.setHr(s2);

                    dauinfos.add(dauInfo);
                    jedis.close();
                }
                return dauinfos.iterator();
            }
        });

        // 写入到olap中
        //按照天分割索引，通过索引模板控制mapping、settings、aliases等.
        dauinfoDStream.foreachRDD(dstream -> {
            dstream.foreachPartition(dauinfos -> {
                while (dauinfos.hasNext()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    DauInfo next = dauinfos.next();
                    String value = JSON.toJSONString(next, new SerializeConfig(true));
                    String user_id = next.getUser_id();
                    Long ts = next.getTs();
                    String format = sdf.format(new Date(ts));
                    String indexName = "gmall_dau_info_" + format;

                    // 写到ES中
                    MyEsUtils.bulkSave(indexName, user_id, value);
                }
            });
        });

        // 8.提交 offset
        DStream.foreachRDD(rdd -> {
            OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
            MyOffsetsUtils.saveOffset(redisOffsetKey, offsetRanges);
        });

        jssc.start();
        jssc.awaitTermination();
    }

    public static void revertState() throws IOException {
        //从ES中查询到所有的mid
        LocalDate date = LocalDate.now();
        String indexName = "gmall_dau_info_" + date;
        String fieldName = "mid";
        List<String> mids = MyEsUtils.searchField(indexName, fieldName);
        //删除redis中记录的状态（所有的mid）
        Jedis jedis = MyRedisUtils.getJedisFromPool();
        String redisKey = "DAU:" + date;
        jedis.del(redisKey);
        //将从ES中查询到的mid覆盖到Redis中
        if (mids != null && mids.size() > 0) {
            Pipeline pipelined = jedis.pipelined();
            for (String mid : mids) {
                // 不会直接在redis中执行
                jedis.sadd(redisKey, mid);
            }
            pipelined.sync();
        }
        jedis.close();
    }
}
