package app;

/**
 * @author Akang
 * @create 2022-11-24 23:58
 */

import bean.PageActionLog;
import bean.PageDisplayLog;
import bean.PageLog;
import bean.StartLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import org.apache.spark.streaming.kafka010.*;
import util.MyConfig;
import util.MyKafkaUtils;
import util.MyOffsetsUtils;

import java.util.*;

/**
 * 日志数据的消费分流
 * 1. 准备实时处理环境 JavaStreamingContext
 * <p>
 * 2. 从Kafka中消费数据
 * <p>
 * 3. 处理数据
 * 3.1 转换数据结构
 * 专用结构  Bean
 * 通用结构  Map JsonObject
 * 3.2 分流
 * <p>
 * 4. 写出到DWD层
 */
public class BaseLogAppStreaming {
    static String redisKey = "offset:ODS_BASE_LOG_SPARK_STREAMING:ODS_LOG_GROUP";

    public static void main(String[] args) throws Exception {
        // Create context with a 2 seconds batch interString
        SparkConf sparkConf = new SparkConf().setMaster("local[*]").setAppName("ods_log_run");
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));

        // 使用指定的 offset 进行消费(offset保存在redis中)
        Map<TopicPartition, Long> offsets = MyOffsetsUtils.readOffset("ods_base_log_sparkstreaming", redisKey);
        // 3.消费kafka 数据
        JavaInputDStream<ConsumerRecord<String, String>> messages = null;
        if (offsets != null && !offsets.isEmpty()) {
            messages = MyKafkaUtils.getKafkaDStream(
                    jssc,
                    "ods_base_log_sparkstreaming",
                    MyConfig.KAFKA_ODS_LOG_GROUP_ID,
                    offsets);
        } else {
            messages = MyKafkaUtils.getKafkaDStream(
                    jssc,
                    "ods_base_log_sparkstreaming",
                    MyConfig.KAFKA_ODS_LOG_GROUP_ID);
        }

        // 3 .处理数据 ,转换为JSON格式
        JavaDStream<JSONObject> messagesJson = messages.mapPartitions(mIterator -> {
            List<JSONObject> messageList = new ArrayList<>();
            while (mIterator.hasNext()) {
                String message = mIterator.next().value();
                JSONObject jsonObject = JSON.parseObject(message);
                messageList.add(jsonObject);
            }
            return messageList.iterator();
        });

        messagesJson.print(10);

        // 4. 数据分流
        String DWD_PAGE_LOG_TOPIC = "DWD_PAGE_LOG_TOPIC";
        String DWD_PAGE_DISPLAY_TOPIC = "DWD_PAGE_DISPLAY_TOPIC";
        String DWD_PAGE_ACTION_TOPIC = "DWD_PAGE_ACTION_TOPIC";
        String DWD_START_LOG_TOPIC = "DWD_START_LOG_TOPIC";
        String DWD_ERROR_LOG_TOPIC = "DWD_ERROR_LOG_TOPIC";

        messagesJson.foreachRDD(new VoidFunction<JavaRDD<JSONObject>>() {
            @Override
            public void call(JavaRDD<JSONObject> data) throws Exception {
                data.foreachPartition(new VoidFunction<Iterator<JSONObject>>() {
                    @Override
                    public void call(Iterator<JSONObject> listData) throws Exception {
                        while (listData.hasNext()) {
                            //分流错误数据
                            JSONObject next = listData.next();
                            JSONObject err = next.getJSONObject("err");
                            if (err != null) {
                                MyKafkaUtils.send(DWD_ERROR_LOG_TOPIC, err.toJSONString());
                            } else {
                                // 提取公共字段
                                JSONObject common = next.getJSONObject("common");
                                String ar = common.getString("ar");
                                String uid = common.getString("uid");
                                String os = common.getString("os");
                                String ch = common.getString("ch");
                                String isNew = common.getString("is_new");
                                String md = common.getString("md");
                                String mid = common.getString("mid");
                                String vc = common.getString("vc");
                                String ba = common.getString("ba");
                                // 提取时间戳
                                Long ts = next.getLong("ts");

                                // 页面数据
                                JSONObject page = next.getJSONObject("page");
                                if (page != null) {
                                    //提取page字段
                                    String pageId = page.getString("page_id");
                                    String pageItem = page.getString("item");
                                    String pageItemType = page.getString("item_type");
                                    Long duringTime = page.getLong("during_time");
                                    String lastPageId = page.getString("last_page_id");
                                    String sourceType = page.getString("source_type");

                                    // 封装 pagelog
                                    PageLog pageLog = new PageLog(mid, uid, ar, ch, isNew, md, os, vc, ba, pageId, lastPageId, pageItem,
                                            pageItemType, duringTime, sourceType, ts);
                                    MyKafkaUtils.send(DWD_PAGE_LOG_TOPIC, JSON.toJSONString(pageLog));


                                    // 提取曝光数据
                                    JSONArray jsonArray = next.getJSONArray("displays");
                                    if (jsonArray != null && jsonArray.size() > 0) {
                                        for (int i = 0; i < jsonArray.size(); i++) {
                                            JSONObject displays = jsonArray.getJSONObject(i);
                                            String displayType = displays.getString("display_type");
                                            String displayItem = displays.getString("item");
                                            String displayItemType = displays.getString("item_type");
                                            String posId = displays.getString("pos_id");
                                            String order = displays.getString("order");
                                            // 封装 PageDisplay
                                            PageDisplayLog pageDisplay = new PageDisplayLog(
                                                    mid, uid, ar, ch, isNew, md, os, vc, ba, pageId, lastPageId, pageItem, pageItemType
                                                    , duringTime, sourceType, displayType, displayItem, displayItemType, order, posId,
                                                    ts);
                                            MyKafkaUtils.send(DWD_PAGE_DISPLAY_TOPIC, JSON.toJSONString(pageDisplay));
                                        }
                                    }
                                    //提取事件数据
                                    JSONArray actions = next.getJSONArray("actions");
                                    if (actions != null && actions.size() > 0) {
                                        for (int i = 0; i < actions.size(); i++) {
                                            JSONObject action = actions.getJSONObject(i);
                                            //提取字段
                                            String actionId = action.getString("action_id");
                                            String actionItem = action.getString("item");
                                            String actionItemType = action.getString("item_type");
                                            Long actionTs = action.getLong("ts");

                                            //封装事件
                                            PageActionLog pageActionLog = new PageActionLog(mid, uid, ar, ch, isNew, md, os, vc, ba,
                                                    pageId, lastPageId, pageItem, pageItemType, duringTime, sourceType, actionId,
                                                    actionItem, actionItemType, actionTs, ts);
                                            MyKafkaUtils.send(DWD_PAGE_ACTION_TOPIC, JSON.toJSONString(pageActionLog));
                                        }
                                    }
                                }

                                //启动数据
                                JSONObject start = next.getJSONObject("start");
                                if (start != null) {
                                    //提取字段
                                    String entry = start.getString("entry");
                                    Long loadingTime = start.getLong("loading_time");
                                    String openAdId = start.getString("open_ad_id");
                                    Long openAdMs = start.getLong("open_ad_ms");
                                    Long openAdSkipMs = start.getLong("open_ad_skip_ms");

                                    // 封装数据
                                    StartLog startLog = new StartLog(mid, uid, ar, ch, isNew, md, os, vc, ba, entry, openAdId,
                                            loadingTime, openAdMs, openAdSkipMs, ts);
                                    MyKafkaUtils.send(DWD_START_LOG_TOPIC, JSON.toJSONString(startLog));
                                }
                            }
                        }
                        MyKafkaUtils.flush();
                    }
                });
            }
        });

        // 提交 offset
        messages.foreachRDD(rdd -> {
            OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
            MyOffsetsUtils.saveOffset(redisKey, offsetRanges);
        });

        jssc.start();
        jssc.awaitTermination();
    }

}
