package com.dongguo.dianping;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dongguo.dianping","com.dongguo.redis"})//com.dongguo.redis"为core组件扫描的包目录
@MapperScan("com.dongguo.dianping.mapper")
public class RedisDianpingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisDianpingApplication.class, args);
		System.out.println("http://127.0.0.1:8081/swagger-ui.html");
	}
}
