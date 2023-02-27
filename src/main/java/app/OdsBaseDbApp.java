package app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;
import redis.clients.jedis.Jedis;
import util.MyConfig;
import util.MyKafkaUtils;
import util.MyOffsetsUtils;
import util.MyRedisUtils;

import java.util.*;

/**
 * @author Akang
 * @create 2022-12-14 11:29
 */
public class OdsBaseDbApp {
    private static String topicName = "ods_base_db_sparkstreaming";
    private static String groupId = "ods_db_group";
    private static String offsetKey = "offset:ods_base_db_sparkstreaming:" + groupId;

    public static void main(String[] args) throws Exception {
        // 1. 创建sparkstraming 执行环境
        SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("ods_base_db_sparkstreaming");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(2));

        // 从redis中读取偏移量
        Map<TopicPartition, Long> offsets = MyOffsetsUtils.readOffset(topicName, offsetKey);

        // 2. 获取数据源，从kafka中消费流式数据
        JavaInputDStream<ConsumerRecord<String, String>> kafkaStream = null;
        if (offsets != null && !offsets.isEmpty()) {
            kafkaStream = MyKafkaUtils.getKafkaDStream(jssc,topicName,groupId,offsets) ;
        } else {
            kafkaStream = MyKafkaUtils.getKafkaDStream(jssc,topicName,groupId) ;

        }

        // 数据转换，转换为JSON格式
        JavaDStream<JSONObject> jsonStream = kafkaStream.map(rdd -> {
            JSONObject jsonStr = JSON.parseObject(rdd.value());
            return jsonStr;
        });
        // 打印输出
        jsonStream.print(2);

        // 根据表明对数据分流
        jsonStream.foreachRDD(
                rdd -> {
                    String redisFactKeys = "FACT:TABLES";
                    String redisDimKeys = "DIM:TABLES";
                    // sadd FACT:TABLES order_info order_detail
                    // sadd DIM:TABLES user_info base_province
                    Jedis jedis = MyRedisUtils.getJedisFromPool();
                    //事实表清单
                    Set<String> factTables = jedis.smembers(redisFactKeys);
                    //维度表清单
                    Set<String> dimTables = jedis.smembers(redisDimKeys);
                    //做成广播变量
                    Broadcast<Set<String>> factTablesBc = jssc.sparkContext().broadcast(factTables);
                    Broadcast<Set<String>> dimTablesBC = jssc.sparkContext().broadcast(dimTables);
                    jedis.close();

                    rdd.foreachPartition(new VoidFunction<Iterator<JSONObject>>() {
                        @Override
                        public void call(Iterator<JSONObject> jsonObjectIterator) throws Exception {
                            Jedis jedis = MyRedisUtils.getJedisFromPool();
                            while (jsonObjectIterator.hasNext()){
                                JSONObject next = jsonObjectIterator.next();
                                // 提取操作类型
                                String type = next.getString("type");
                                String opValue = "" ;
                                switch (type){
                                    case "bootstrap-insert":
                                        opValue = "I" ;
                                        break;
                                    case "insert":
                                        opValue ="I" ;
                                        break;
                                    case "update":
                                        opValue = "U" ;
                                        break ;
                                    case "delete":
                                        opValue ="D";
                                        break;
                                    default:
                                        opValue = "" ;
                                        break;
                                }
                                if (opValue!=null && opValue.length()> 0){
                                    String tableName = next.getString("table");
                                    if (factTablesBc.value().contains(tableName)){
                                        String data = next.getString("data");
                                        String dwdTopicName = "DWD_" + tableName.toUpperCase() + "_" + opValue ;
                                        MyKafkaUtils.send(dwdTopicName,data);
                                    }
                                    if (dimTablesBC.value().contains(tableName)){
                                        JSONObject data = next.getJSONObject("data");
                                        String id = data.getString("id");
                                        String redisKey = "DIM:" + tableName.toUpperCase() + ":" + id ;

                                        jedis.set(redisKey,data.toJSONString()) ;
                                    }
                                }
                            }
                            jedis.close();
                            MyKafkaUtils.flush();
                        }
                    });
                }
        );
        // 提交偏移量
        kafkaStream.foreachRDD(rdd -> {
            OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
            // 保存偏移量
            MyOffsetsUtils.saveOffset(offsetKey, offsetRanges);
        });
        // 启动程序
        jssc.start();
        jssc.awaitTermination();

    }
}
