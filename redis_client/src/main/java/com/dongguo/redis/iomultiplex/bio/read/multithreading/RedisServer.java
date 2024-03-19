package com.dongguo.redis.iomultiplex.bio.read.multithreading;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端
 */
public class RedisServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6379);
        while (true) {
            System.out.println("1 等待客户端连接中");
            Socket socket = serverSocket.accept();
            System.out.println("2 客户端连接成功");
            new Thread(() -> {
                try {
                    InputStream inputStream = socket.getInputStream();
                    int length = -1;
                    byte[] bytes = new byte[1024];
                    System.out.println("3  等待读取");
                    while ((length = inputStream.read(bytes)) != -1) {
                        System.out.println("读取成功" + new String(bytes, 0, length));
                        System.out.println("=========================================");
                    }
                    inputStream.close();
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, Thread.currentThread().getName()).start();
        }
    }
}
