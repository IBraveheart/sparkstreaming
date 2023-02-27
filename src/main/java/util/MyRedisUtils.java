package util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Akang
 * @create 2022-11-30 11:30
 */
public class MyRedisUtils {
    private static final String REDIS_HOST = "hadoop101";
    private static final Integer REDIS_PORT = 6379;

    private static JedisPool jedisPool = null;

    public static Jedis getJedisFromPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        //最大连接数
        config.setMaxTotal(100);
        //最大空闲
        config.setMaxIdle(20);
        //最小空闲
        config.setMinIdle(20);
        //忙碌时是否等待
        config.setBlockWhenExhausted(true);
        //忙碌时等待时长 毫秒
        config.setMaxWaitMillis(5000);
        //每次获得连接的进行测试
        config.setTestOnBorrow(false);
        //config.setTestOnCreate(false);

        if (jedisPool ==null){
            jedisPool = new JedisPool(config, REDIS_HOST, REDIS_PORT);
        }
        return jedisPool.getResource();
    }

    /**
     * redis 测试
     */

    public static void testKeys() {
        Jedis jedis = getJedisFromPool();
        Set<String> keys = jedis.keys("*");
        System.out.println(keys);
    }

    /**
     * @param
     */
    public static void testString() {
        Jedis jedis = getJedisFromPool();
        jedis.set("name", "redis");
    }

    public static void testList() {
        Jedis jedis = getJedisFromPool();
        jedis.lpush("test:list", "hadoop", "scala", "spark", "sqoop", "flink", "kafka");
    }

    public static List<String> seachList(){
        Jedis jedis = getJedisFromPool();
        List<String> lrange = jedis.lrange("test:list", 0, jedis.llen("test:list"));
        return lrange ;
    }

    public static void testHset(){
        Jedis jedis = getJedisFromPool();
        String key = "test:hset" ;
        Map<String,String> hash = new HashMap<>() ;
        hash.put("name","tom") ;
        jedis.hset(key, hash) ;
    }

    public static void main(String[] args) {
        testHset();
    }

}
