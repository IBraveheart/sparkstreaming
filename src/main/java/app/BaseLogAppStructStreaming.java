package app;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import util.MyConfig;



/**
 * @author Akang
 * @create 2022-11-25 15:46
 */
public class BaseLogAppStructStreaming {
    public static void main(String[] args) throws Exception {
        // 1. 构建执行环境
        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("ods_log_run")
                .getOrCreate();


        // 2. 从 kafka 读数据
        Dataset<String> df = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", MyConfig.KAFKA_BOOTSTRAP_SERVERS)
                .option("subscribe", "ODS_BASE_LOG_sparkstreaming")
                .option("startingOffsets", "earliest")
                .load()
                .as(Encoders.STRING());

        // 3. 打印数据
        df.writeStream()
                .outputMode("append")
                .format("console")
                .start()
                .awaitTermination();



    }
}
