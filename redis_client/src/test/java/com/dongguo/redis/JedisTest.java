package com.dongguo.redis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
public class JedisTest {
    @Test
    public void redisClientTest() {
        Jedis jedis = new Jedis("192.168.122.131", 6379);
        jedis.auth("root");
        System.out.println(jedis.ping());
    }

    @Test
    public void redisStringTest() {
        Jedis jedis = new Jedis("192.168.122.131", 6379);
        jedis.auth("root");
        jedis.set("k1", "v1");
        String value = jedis.get("k1");
        System.out.println(value);
    }

    @Test
    public void redisListTest() {
        Jedis jedis = new Jedis("192.168.122.131", 6379);
        jedis.auth("root");
        jedis.lpush("list", "11", "22", "33");
        List<String> list = jedis.lrange("list", 0, -1);
        list.forEach(System.out::println);
    }

    @Test
    public void redisHashTest() {
        Jedis jedis = new Jedis("192.168.122.131", 6379);
        jedis.auth("root");
        jedis.hset("user", "name", "zhangsan");
        jedis.hset("user", "age", "18");
        Map<String, String> user = jedis.hgetAll("user");
        System.out.println(user);
    }

    @Test
    public void redisSetTest() {
        Jedis jedis = new Jedis("192.168.122.131", 6379);
        jedis.auth("root");
        jedis.sadd("s1","1","1","2","3","3");
        Set<String> set = jedis.smembers("s1");
        System.out.println(set);
    }

    @Test
    public void redisZsetTest() {
        Jedis jedis = new Jedis("192.168.122.131", 6379);
        jedis.auth("root");
        jedis.zadd("zset", 50,"v1");
        jedis.zadd("zset", 80,"v2");
        jedis.zadd("zset", 40,"v3");
        jedis.zadd("zset", 70,"v4");
        List<Tuple> zset = jedis.zrangeWithScores("zset", 0, -1);
        System.out.println(zset);
    }
}
