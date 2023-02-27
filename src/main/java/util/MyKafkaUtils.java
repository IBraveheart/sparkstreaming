package util;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Akang
 * @create 2022-11-24 17:50
 */
public class MyKafkaUtils {
    private static Map<String, Object> kafkaConsumerParams = new HashMap<>();
    private static Map<String, Object> kafkaProducerParams = new HashMap<>();
    private static KafkaProducer<String, String> producer = null;

    static {
        kafkaConsumerParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop101:9092,hadoop102:9092,hadoop103:9092");
        //kv序列化器
        kafkaConsumerParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConsumerParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //自动提交offset
        kafkaConsumerParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        //offset重置  "latest"  "earliest"
        kafkaConsumerParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    static {
        kafkaProducerParams.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop101:9092,hadoop102:9092,hadoop103:9092");
        kafkaProducerParams.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducerParams.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducerParams.put(ProducerConfig.ACKS_CONFIG, "all");
        kafkaProducerParams.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        producer = new KafkaProducer<String, String>(kafkaProducerParams);
    }

    /**
     * 基于SparkStreaming消费 ,获取到KafkaDStream , 使用默认的offset
     *
     * @param jssc
     * @param topic
     * @param GroupID
     * @return
     */
    public static JavaInputDStream<ConsumerRecord<String, String>> getKafkaDStream(
            JavaStreamingContext jssc, String topic, String GroupID) {
        kafkaConsumerParams.put(ConsumerConfig.GROUP_ID_CONFIG, GroupID);
        JavaInputDStream<ConsumerRecord<String, String>> kafkaStream = KafkaUtils.createDirectStream(
                jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(Arrays.asList(topic), kafkaConsumerParams));
        return kafkaStream;
    }

    /**
     * 基于SparkStreaming消费 ,获取到KafkaDStream , 使用指定的offset
     *
     * @param jssc
     * @param topic
     * @param GroupID
     * @param offsets
     * @return
     */

    public static JavaInputDStream<ConsumerRecord<String, String>> getKafkaDStream(
            JavaStreamingContext jssc, String topic, String GroupID, Map<TopicPartition, Long> offsets) {
        kafkaConsumerParams.put(ConsumerConfig.GROUP_ID_CONFIG, GroupID);
        JavaInputDStream<ConsumerRecord<String, String>> kafkaStream = KafkaUtils.createDirectStream(
                jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(Arrays.asList(topic), kafkaConsumerParams, offsets)
        );
        return kafkaStream;
    }


    /**
     * kafka 生产消息（按照默认的黏性分区策略）
     *
     * @param topic
     * @param msg
     */
    public static void send(String topic, String msg) {
        producer.send(new ProducerRecord<>(topic, msg));
    }

    /**
     * kafka 生产消息（按照key进行分区策略）
     *
     * @param topic
     * @param key
     * @param msg
     */

    public static void send(String topic, String key, String msg) {
        producer.send(new ProducerRecord<>(topic, key, msg));
    }

    /**
     * 关闭 生产对象
     */
    public static void close() {
        if (producer != null) {
            producer.close();
        }
    }

    /**
     * 刷写，将缓存区数据写到磁盘
     */

    public static void flush() {
        producer.flush();
    }

    public static void main(String[] args) {
        System.out.println(MyKafkaUtils.producer);
    }
}
