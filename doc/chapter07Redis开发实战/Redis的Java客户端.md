在Redis官网中提供了各种语言的客户端，地址：https://redis.io/docs/clients/

![image-20240307182038371](https://gitee.com/dongguo4812_admin/image/raw/master/image/202403071820944.png)

其中Java客户端也包含很多：

![image-20240307182104504](https://gitee.com/dongguo4812_admin/image/raw/master/image/202403071821738.png)

- Jedis和Lettuce：这两个主要是提供了Redis命令对应的API，方便我们操作Redis，而SpringDataRedis又对这两种做了抽象和封装，因此我们后期会直接以SpringDataRedis来学习。

  ![image-20240307182144391](https://gitee.com/dongguo4812_admin/image/raw/master/image/202403071821697.png)

- Redisson：是在Redis基础上实现了分布式的可伸缩的java数据结构，例如Map、Queue等，而且支持跨进程的同步机制：Lock、Semaphore等待，比较适合用来实现特殊的功能需求。

# 集成Jedis

[Jedis的官网地址](https://github.com/redis/jedis)

Jedis Client是Redis官网推荐的一个面向java客户端，库文件实现了对各类API进行封装调用

## 建Module

建立一个新的springboot module:redis_client

## pom添加jedis依赖

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

## 测试类

尝试连接redis客户端。

```java
    @Test
    public void redisClientTest(){
        Jedis jedis = new Jedis("192.168.122.131", 6379);
        jedis.auth("root");
        System.out.println(jedis.ping());
    }
运行结果：PONG
```

## string

```java
    @Test
    public void redisStringTest(){
        Jedis jedis = new Jedis("192.168.122.131", 6379);
        jedis.auth("root");
        jedis.set("k1", "v1");
        String value = jedis.get("k1");
        System.out.println(value);
    }
运行结果：v1
```

## list

```java
    @Test
    public void redisListTest(){
        Jedis jedis = new Jedis("192.168.122.131", 6379);
        jedis.auth("root");
        jedis.lpush("list", "11","22","33");
        List<String> list = jedis.lrange("list", 0, -1);
        list.forEach(System.out::println);
    }
运行结果：
33
22
11
```

## hash

```java
    @Test
    public void redisHashTest() {
        Jedis jedis = new Jedis("192.168.122.131", 6379);
        jedis.auth("root");
        jedis.hset("user", "name", "zhangsan");
        jedis.hset("user", "age", "18");
        Map<String, String> user = jedis.hgetAll("user");
  		System.out.println(user);
    }
运行结果：{name=zhangsan, age=18}
```

## set

```java
    @Test
    public void redisSetTest() {
        Jedis jedis = new Jedis("192.168.122.131", 6379);
        jedis.auth("root");
        jedis.sadd("s1","1","1","2","3","3");
        Set<String> set = jedis.smembers("s1");
        System.out.println(set);
    }
运行结果：[1, 2, 3]
```

## zset

```java
    @Test
    public void redisZsetTest() {
        Jedis jedis = new Jedis("192.168.122.131", 6379);
        jedis.auth("root");
        jedis.zadd("zset", 50,"v1");
        jedis.zadd("zset", 80,"v2");
        jedis.zadd("zset", 40,"v3");
        jedis.zadd("zset", 70,"v4");
        List<Tuple> zset = jedis.zrangeWithScores("zset", 0, -1);
        System.out.println(zset);
    }
运行结果:[[v3,40.0], [v1,50.0], [v4,70.0], [v2,80.0]]
```

# 集成lettuce

Lettuce是一个Redis的Java驱动包，Lettuce翻译为生菜![image-20240307214516066](https://gitee.com/dongguo4812_admin/image/raw/master/image/202403072145567.png)



jedis和Lettuce都是Redis的客户端，它们都可以连接Redis服务器，但是在SpringBoot2.0之后默认都是使用的Lettuce这个客户端连接Redis服务器。因为当使用Jedis客户端连接Redis服务器的时候，每个线程都要拿自己创建的Jedis实例去连接Redis客户端，当有很多个线程的时候，不仅开销大需要反复的创建关闭一个Jedis连接，而且也是线程不安全的，一个线程通过Jedis实例更改Redis服务器中的数据之后会影响另一个线程；

但是如果使用Lettuce这个客户端连接Redis服务器的时候，就不会出现上面的情况，Lettuce底层使用的是Netty,当有多个线程都需要连接Redis服务器的时候，可以保证只创建一个Lettuce连接，使所有的线程共享这一个Lettuce连接，这样可以减少创建关闭一个Lettuce连接时候的开销；而且这种方式也是线程安全的，不会出现一个线程通过Lettuce更改Redis服务器中的数据之后而影响另一个线程的情况.

## 引入lettuce的依赖

```xml
        <dependency>
            <groupId>io.lettuce</groupId>
            <artifactId>lettuce-core</artifactId>
        </dependency>
```

## 测试类

```java
    @Test
    public void lettuceClientTest(){
        //使用构建器builder
        RedisURI uri = RedisURI.builder()
                .withHost("192.168.122.131")
                .withPort(6379)
                .withAuthentication("default", "root")
                .build();
        //创建客户端
        RedisClient client = RedisClient.create(uri);
        //建立连接
        StatefulRedisConnection<String, String> conn = client.connect();

        //创建操作的command
        RedisCommands<String, String> commands = conn.sync();
        commands.set("k1", "v1");
        System.out.println("k1:" +commands.get("k1"));

        //关闭连接
        conn.close();
        //关闭客户端
        client.shutdown();
    }
运行结果：k1:v1
```

