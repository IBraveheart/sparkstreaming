package util;

import org.apache.kafka.common.TopicPartition;
import org.apache.spark.streaming.kafka010.OffsetRange;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Akang
 * @create 2022-12-14 16:26
 */
public class MyOffsetsUtils {

    public static void saveOffset(String offsetKey, OffsetRange[] offsetRanges) {
        Map<String, String> offsets = new HashMap<>();
        if (offsetRanges != null && offsetRanges.length > 0) {
            for (OffsetRange e : offsetRanges) {
                String topic = e.topic();
                int partition = e.partition();
                long endOffset = e.untilOffset();
                offsets.put(String.valueOf(partition), String.valueOf(endOffset));
            }
            //提交offset
            Jedis jedis = MyRedisUtils.getJedisFromPool();
            jedis.hset(offsetKey, offsets);
            jedis.close();
        }
    }

    public static Map<TopicPartition, Long> readOffset(String topic ,String offsetKey){
        Jedis jedis = MyRedisUtils.getJedisFromPool();
        Map<String, String> offsets = jedis.hgetAll(offsetKey);
        Map<TopicPartition,Long> tp = new HashMap<>() ;

        Set<Map.Entry<String, String>> entries = offsets.entrySet();
        for (Map.Entry<String,String> e : entries){
            String partition = e.getKey();
            String endOffset = e.getValue();
            tp.put(new TopicPartition(topic,Integer.valueOf(partition)),Long.valueOf(endOffset));
        }
        return tp ;
    }
}
