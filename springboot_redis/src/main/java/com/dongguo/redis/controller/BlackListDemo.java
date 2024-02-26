package com.dongguo.redis.controller;

import cn.hutool.core.util.StrUtil;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * 布隆过滤备实现 白名单
 */
public class BlackListDemo {
    private static final int COUNT = 10000 * 100;
    //误判率  大于0小于1.0
    private static final double FPP = 0.03;
    private static RBloomFilter<String> bloomFilter = null;
    private static RedissonClient redissonClient = null;

    static {
        Config configs = new Config();
        configs.useSingleServer().setAddress("redis://192.168.122.128:6379").setPassword("root").setDatabase(0);
        redissonClient = Redisson.create(configs);
        //初始化布隆过滤器
        bloomFilter = redissonClient.getBloomFilter("blackListBloomFilter", new StringCodec());
        bloomFilter.tryInit(COUNT, FPP);
    }

    public static void main(String[] args) {
        String result = getBlackList("1001");
        System.out.println(result);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        redissonClient.shutdown();
    }

    private static String getBlackList(String videoId) {
        boolean contains = bloomFilter.contains(videoId);
        if (contains) {
            RBucket<String> bucket = redissonClient.getBucket(videoId, new StringCodec());
            String result = bucket.get();
            if (StrUtil.isBlank(result)) {
                return "缓存中不存在该数据";
            }
            return "缓存中查找到该数据：" + result;
        } else {
            //不存在，添加到布隆过滤器
            bloomFilter.add(videoId);
            redissonClient.getBucket(videoId, new StringCodec()).set(videoId + "我发布的视频");
            return "布隆过滤器中不存在该数据";
        }
    }
}
