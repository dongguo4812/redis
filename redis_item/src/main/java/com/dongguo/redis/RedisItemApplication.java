package com.dongguo.redis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dongguo.redis.mapper")
public class RedisItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisItemApplication.class, args);
        System.out.println("http://127.0.0.1:8081/swagger-ui.html");
    }

}
