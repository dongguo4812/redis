package com.dongguo.redis.config;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.nio.charset.StandardCharsets;

/**
 * @Author: Administrator
 * @Date: 2024-03-11
 */
@Configuration
@Slf4j
public class GuavaBloomFilterConfig {
    //布隆过滤器预计插入的元素数量
    private static final int COUNT = 1000;
    //误判率  大于0小于1.0。值越小误判的个数也就越少
    private static final double FPP = 0.03;
    @Bean
    public BloomFilter<String> bloomFilterFactory(){
        return BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), COUNT, FPP);
    }
}
