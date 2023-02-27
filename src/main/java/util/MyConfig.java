package util;

/**
 * @author Akang
 * @create 2022-11-24 18:02
 */
public class MyConfig {
    public static String KAFKA_BOOTSTRAP_SERVERS = "hadoop101:9092,hadoop102:9092,hadoop103:9092";
    public static String KAFKA_ODS_LOG_GROUP_ID = "ODS_LOG_GROUP" ;
    public static String KAFKA_ODS_LOG_TOPIC ="ODS_BASE_LOG_sparkstreaming" ;
//    private static Boolean

    public static String REDIS_HOST = "redis.host";
    public static String REDIS_PORT = "redis.port";

    public static String ES_HOST = "es.host";
    public static String ES_PORT = "es.port";


}
