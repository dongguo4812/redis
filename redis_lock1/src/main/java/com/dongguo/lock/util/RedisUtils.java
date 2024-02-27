package com.dongguo.lock.util;

import cn.hutool.core.util.ObjectUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author: Administrator
 * @Date: 2024-02-27
 */
public class RedisUtils {
    private static JedisPool jedisPool;
    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(10);
        jedisPool = new JedisPool(jedisPoolConfig, "192.168.122.128", 6379);
    }

    public static Jedis getJedisPool() throws Exception {
        if (ObjectUtil.isNotNull(jedisPool)){
            return jedisPool.getResource();
        }
        throw new Exception("JedisPool is not exists");
    }
}
