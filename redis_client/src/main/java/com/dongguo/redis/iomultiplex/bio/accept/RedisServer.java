package com.dongguo.redis.iomultiplex.bio.accept;

import java.io.IOException;
import java.net.ServerSocket;


/**
 * 服务端
 */
public class RedisServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6379);

        while (true){
            System.out.println("1 模拟RedisServer启动，等待客户端连接中");
            serverSocket.accept();
            System.out.println("2 客户端连接成功");
            System.out.println("=============================");
        }
    }
}
