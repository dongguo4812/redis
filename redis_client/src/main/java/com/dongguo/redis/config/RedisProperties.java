package com.dongguo.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedisProperties {

    private String host;
    private int port;
    private String password;
    private int maxActive;
    private String maxWait;
    private int maxIdle;
    private int minIdle;
    private int database;

    // getter和setter方法
}
