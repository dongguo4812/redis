package com.dongguo.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LettuceTest {
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
}
