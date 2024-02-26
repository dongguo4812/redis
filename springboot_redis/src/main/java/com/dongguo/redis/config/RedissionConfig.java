package com.dongguo.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;


@Configuration
public class RedissionConfig {

    @Value("${spring.data.redis.host}")
    String host;
    @Value("${spring.data.redis.port}")
    String port;
    @Value("${spring.data.redis.password}")
    String pass;
    private static final int COUNT = 10000 * 100;
    //误判率  大于0小于1.0
    private static final double FPP = 0.03;

    public Config config() {
        Config configs = new Config();
        //连接间隔 心跳 客户端长时间未使用，服务端会断开
        String addr = "redis://" + host + ":" + port;
        configs.useSingleServer().setAddress(addr).setClientName("redission").setPassword(pass).setDatabase(0);
        return configs;
    }

    @Bean
    public RedissonClient redissonClient() {
        RedissonClient redissonClient = Redisson.create(config());
        return redissonClient;

    }

    @Bean
    public RBloomFilter bloomFilter() {
        //初始化布隆过滤器
        RBloomFilter<Object> bloomFilter = redissonClient().getBloomFilter("phoneNumBloomFilter", new StringCodec());
        bloomFilter.tryInit(COUNT, FPP);

        return bloomFilter;

    }
}