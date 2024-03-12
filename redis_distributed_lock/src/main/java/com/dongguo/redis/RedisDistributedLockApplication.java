package com.dongguo.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisDistributedLockApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisDistributedLockApplication.class, args);
		System.out.println("http://127.0.0.1:8081/swagger-ui.html");
	}

}
