package com.dongguo.redis.iomultiplex.bio.accept;

import java.io.IOException;
import java.net.Socket;

/**
 * 客户端1
 */
public class RedisClient1 {
    public static void main(String[] args) throws IOException {
        System.out.println("RedisClient1 请求连接");
        Socket socket = new Socket("127.0.0.1", 6379);
        System.out.println("RedisClient1 连接结束");
    }
}
