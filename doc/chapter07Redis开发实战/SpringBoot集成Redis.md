# SpringDataRedis

SpringData是Spring中数据操作的模块，包含对各种数据库的集成，其中对Redis的集成模块就叫做SpringDataRedis，官网地址：https://spring.io/projects/spring-data-redis

- 提供了对不同Redis客户端的整合（Lettuce和Jedis）
- 提供了RedisTemplate统一API来操作Redis
- 支持Redis的发布订阅模型
- 支持Redis哨兵和Redis集群
- 支持基于Lettuce的响应式编程
- 支持基于JDK、JSON、字符串、Spring对象的数据序列化及反序列化
- 支持基于Redis的JDKCollection实现

SpringDataRedis中提供了RedisTemplate工具类，其中封装了各种对Redis的操作。并且将不同数据类型的操作API封装到了不同的类型中：

![image-20240308072202506](https://gitee.com/dongguo4812_admin/image/raw/master/image/202403080722572.png)

## pom引入SpringDataRedis的依赖

Spring Boot 与 Redis 整合包，使用默认的 Lettuce 客户端，本身就带有Lettuce的依赖

![image-20240308072848789](https://gitee.com/dongguo4812_admin/image/raw/master/image/202403080728424.png)

所以我们可以把之前引入的lettuce依赖注释掉。

```xml
<!--        <dependency>-->
<!--            <groupId>io.lettuce</groupId>-->
<!--            <artifactId>lettuce-core</artifactId>-->
<!--        </dependency>-->
        <!--springboot与redis整合包-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
```

## 业务类

### 配置类SwaggerOpenApiConfig

```java
package com.dongguo.redis.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;

@SpringBootConfiguration
public class SwaggerOpenApiConfig {
    /***
     * 构建Swagger3.0文档说明
     * @return 返回 OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {

        // 联系人信息(contact)，构建API的联系人信息，用于描述API开发者的联系信息，包括名称、URL、邮箱等
        // name：文档的发布者名称 url：文档发布者的网站地址，一般为企业网站 email：文档发布者的电子邮箱
        Contact contact = new Contact()
                .name("东郭")                             // 作者名称
                .email("dongguo@qq.com")                   // 作者邮箱
                .url("https://www.dongguo.com")  // 介绍作者的URL地址
                .extensions(new HashMap<String, Object>()); // 使用Map配置信息（如key为"name","email","url"）

        // 授权许可信息(license)，用于描述API的授权许可信息，包括名称、URL等；假设当前的授权信息为Apache 2.0的开源标准
        License license = new License()
                .name("Apache 2.0")                         // 授权名称
                .url("https://www.apache.org/licenses/LICENSE-2.0.html")    // 授权信息
                .identifier("Apache-2.0")                   // 标识授权许可
                .extensions(new HashMap<String, Object>());// 使用Map配置信息（如key为"name","url","identifier"）

        //创建Api帮助文档的描述信息、联系人信息(contact)、授权许可信息(license)
        Info info = new Info()
                .title("Swagger3.0 (Open API) 框架学习示例文档")      // Api接口文档标题（必填）
                .description("学习Swagger框架而用来定义测试的文档")     // Api接口文档描述
                .version("1.0.0")                                  // Api接口版本
                .termsOfService("https://dongguo.com/")            // Api接口的服务条款地址
                .license(license)                                  // 设置联系人信息
                .contact(contact);                                 // 授权许可信息
        // 返回信息
        return new OpenAPI()
                .openapi("3.0.1")  // Open API 3.0.1(默认)
                .info(info);       // 配置Swagger3.0描述信息
    }
}
```



# 连接单机Redis

## application.yml配置

```yaml
server:
  port: 8080
spring:
  application:
    name: redis_client
# ========================redis相关配置=====================
  data:
    redis:
      host: 192.168.122.131
      port: 6379
      password: root
# ========================logging 日志相关的配置=====================
logging:
  level:
    #系统默认，全局root配置的日志形式，可以注释掉
    root: info
    #开发人员自己设置的包结构，对那个package进行什么级别的日志监控
    com.dongguo.redis: info
  file:
    #开发人员自定义日志路径和日志名称
     name: E:/log/redis_client/redis.log
    #%d{HH:mm:ss.SSS}――日志输出时间
    #%thread――输出日志的进程名字，这在Web应用以及异步任务处理中很有用
    #%-5level――日志级别，并且使用5个字符靠左对齐
    #%logger- ――日志输出者的名字
    #%msg――日志消息
    #%n――平台的换行符
    #logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger- %msg%n
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger- %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger- %msg%n"
# ========================swagger=====================
springdoc:
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
```

## 测试

使用SpringDataRedis实现订单新增的功能

### service

```java
package com.dongguo.redis.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.dongguo.redis.utils.CacheKeyUtil.ORDER_KEY;

@Service
@Slf4j
public class OrderService {
    @Resource
    private RedisTemplate redisTemplate;

    public void addOrder() {
        Long orderId = ThreadLocalRandom.current().nextLong(1000) + 1;
        String orderNO = UUID.randomUUID().toString();
        String key = ORDER_KEY + orderId;
        String value = "订单号:" + orderNO;
        redisTemplate.opsForValue().set(key, value);
        log.info("新增订单，订单id:{}，订单No:{}", key, orderNO);
    }

    public String getOrder(Long orderId) {
        String key = ORDER_KEY + orderId;
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            log.info("订单不存在");
        } else {
            log.info((String) obj);
        }
        return (String) obj;
    }
}
```

### controller

```java
package com.dongguo.redis.controller;

import com.dongguo.redis.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Operation(
            summary = "新增订单",
            description = "新增订单"
    )
    @PostMapping("/add")
    public void addOrder(){
        orderService.addOrder();
    }
    @Operation(
            summary = "查询订单",
            description = "查询订单"
    )
    @GetMapping("/get/{orderId}")
    public String getOrder(@PathVariable(value = "orderId") Long orderId){
       return orderService.getOrder(orderId);
    }
}
```

启动项目，访问swagger：http://127.0.0.1:8080/swagger-ui/index.html





## 新增订单









## 查询订单























# 连接Redis集群