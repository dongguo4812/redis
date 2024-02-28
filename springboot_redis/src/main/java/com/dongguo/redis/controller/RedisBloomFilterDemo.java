package com.dongguo.redis.controller;

import cn.hutool.core.util.ObjectUtil;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
 * @Author: Administrator
 * @Date: 2024-02-26
 * redis布隆过滤器demo
 */
public class RedisBloomFilterDemo {
    @Value("${spring.data.redis.host}")
    private static String host;

    @Value("${spring.data.redis.port}")
    private static String port;
    private static final int COUNT = 10000 * 100;
    //误判率  大于0小于1.0
    private static final double FPP = 0.03;
    private static RBloomFilter<String> bloomFilter = null;
    private static RedissonClient redissonClient = null;
    private static HashMap<String, String> map = null;

    static {
        Config configs = new Config();
        configs.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword("root").setDatabase(0);
        redissonClient = Redisson.create(configs);
        //初始化布隆过滤器
        bloomFilter = redissonClient.getBloomFilter("phoneNumBloomFilter", new StringCodec());
        bloomFilter.tryInit(COUNT, FPP);
        //布隆过滤器添加元素10086
//        bloomFilter.add("10086");
        //添加缓存key：10086
//        redissonClient.getBucket("10086", new StringCodec()).set("chinaMobile-10086");
        //模拟数据库
        map = new HashMap<>();
        map.put("10086", "chinaMobile-10086");
    }

    public static void main(String[] args) {
        String result = getPhoneNumByList("10086");
        System.out.println(result);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        redissonClient.shutdown();
    }

    private static String getPhoneNumByList(String phoneNum) {
        String result;
        if (bloomFilter.contains(phoneNum)) {
            //存在查redis
            RBucket<String> bucket = redissonClient.getBucket(phoneNum, new StringCodec());
            result = bucket.get();
            if (ObjectUtil.isNotEmpty(result)) {
                return "redis缓存中获得：" + result;
            }
            result = getPhoneNumByMysql(phoneNum);
            if (ObjectUtil.isNotEmpty(result)) {
                redissonClient.getBucket(phoneNum, new StringCodec()).set(result);
                return "数据库中获得: " + result;
            }
            return "未查到该数据";
        }
        return "不存在该数据:" + phoneNum;
    }

    private static String getPhoneNumByMysql(String phoneNum) {
        return map.get(phoneNum);
    }
}
