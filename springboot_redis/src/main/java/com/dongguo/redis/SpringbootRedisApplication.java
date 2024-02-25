package com.dongguo.redis;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.dongguo.redis.mapper") //import tk.mybatis.spring.annotation.MapperScan;
public class SpringbootRedisApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootRedisApplication.class, args);
        System.out.println("http://127.0.0.1:9000/swagger-ui/index.html");
    }
}
