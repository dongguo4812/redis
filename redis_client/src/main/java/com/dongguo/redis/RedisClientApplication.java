package com.dongguo.redis;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dongguo.redis.mapper") //import tk.mybatis.spring.annotation.MapperScan;
public class RedisClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisClientApplication.class, args);
//        System.out.println("http://127.0.0.1:8080/swagger-ui/index.html");
        System.out.println("http://127.0.0.1:8080/swagger-ui.html");
    }
}
