package com.dongguo.redis.iomultiplex.bio.accept;

import java.io.IOException;
import java.net.Socket;

/**
 * 客户端2
 */
public class RedisClient2 {
    public static void main(String[] args) throws IOException {
        System.out.println("RedisClient2 请求连接");
        Socket socket = new Socket("127.0.0.1", 6379);
        System.out.println("RedisClient2 连接结束");
    }
}
