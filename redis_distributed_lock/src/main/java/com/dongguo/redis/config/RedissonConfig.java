package com.dongguo.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {


    /**
     * RedLock 3个各自独立的master
     * @return
     */
    @Bean("redissonClient1")
    RedissonClient redissonClient1(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.122.132:7001").setPassword("root");
        return Redisson.create(config);
    }
    @Bean("redissonClient2")
    RedissonClient redissonClient2(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.122.133:7002").setPassword("root");
        return Redisson.create(config);
    }

    @Bean("redissonClient3")
    RedissonClient redissonClient3(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.122.134:7003").setPassword("root");
        return Redisson.create(config);
    }
}
