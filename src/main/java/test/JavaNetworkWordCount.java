package test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author Akang
 * @create 2022-11-28 15:29
 */
public class JavaNetworkWordCount {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) throws InterruptedException {

        // Create the context with a 1 second batch size
        SparkConf sparkConf = new SparkConf().setAppName("JavaNetworkWordCount").setMaster("local[*]") ;
        JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, Durations.seconds(1));

        JavaReceiverInputDStream<String> lines = ssc.socketTextStream(
                "hadoop101", Integer.parseInt("9999"), StorageLevels.MEMORY_AND_DISK_SER);
        JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(SPACE.split(x)).iterator());
        JavaPairDStream<String, Integer> wordCounts = words.mapToPair(s -> new Tuple2<>(s, 1))
                .reduceByKey((i1, i2) -> i1 + i2);

        wordCounts.print();
        ssc.start();
        ssc.awaitTermination();

    }
}
