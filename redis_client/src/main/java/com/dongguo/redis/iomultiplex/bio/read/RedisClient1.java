package com.dongguo.redis.iomultiplex.bio.read;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * 客户端1
 */
public class RedisClient1 {
    public static void main(String[] args) throws IOException {
        System.out.println("RedisClient1 请求连接");
        Socket socket = new Socket("127.0.0.1", 6379);
        OutputStream outputStream = socket.getOutputStream();
        while (true){
            System.out.println("等待输入");
            Scanner scanner = new Scanner(System.in);
            String next = scanner.next();
            if ("quit".equalsIgnoreCase(next)){
                System.out.println("写入quit，结束连接");
                break;
            }
            socket.getOutputStream().write(next.getBytes());
            System.out.println("写入" + next);
        }
        outputStream.close();
        socket.close();
    }
}
