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

![image-20240308072848789](https://gitee.com/dongguo4812_admin/image/raw/master/image/202403080728424.png)所以我们可以把之前引入的lettuce依赖注释掉。

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



# 单机

































# 集群