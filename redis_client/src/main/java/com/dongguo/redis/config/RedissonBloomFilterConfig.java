package com.dongguo.redis.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RedissonBloomFilterConfig {
    @Resource
    private RedisProperties redisProperties;
    private static final int COUNT = 10000 * 100;
    //误判率  大于0小于1.0
    private static final double FPP = 0.03;

    @Bean(name = "redissonClientFactory")
    public RedissonClient redissonClientFactory() {
        Config configs = new Config();
        String address = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
        configs.useSingleServer().setAddress(address).setPassword(redisProperties.getPassword()).setDatabase(0);
        return Redisson.create(configs);
    }
    @Bean
    public RBloomFilter<String> RBloomFilterFactory(@Qualifier("redissonClientFactory")RedissonClient redissonClient) {
        //初始化布隆过滤器
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("blackList", new StringCodec());
        bloomFilter.tryInit(COUNT, FPP);
        return bloomFilter;
    }
}
