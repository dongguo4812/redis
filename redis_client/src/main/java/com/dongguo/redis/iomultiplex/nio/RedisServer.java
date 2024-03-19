package com.dongguo.redis.iomultiplex.nio;

import io.lettuce.core.StrAlgoArgs;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 服务端
 */
public class RedisServer {
    static ArrayList<SocketChannel> socketChannels = new ArrayList<>();
    static ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    public static void main(String[] args) throws IOException {
        System.out.println("1 服务端启动中");
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 6379));
        serverSocketChannel.configureBlocking(false);//设置为非阻塞模式
        while (true) {
            for (SocketChannel channel : socketChannels) {
                int read = channel.read(byteBuffer);
                if (read > 0) {
                    byteBuffer.flip();
                    byte[] bytes = new byte[read];
                    byteBuffer.get(bytes);
                    System.out.println("读取成功" + new String(bytes, 0, read));
                    byteBuffer.clear();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                System.out.println("连接成功");
                socketChannel.configureBlocking(false);
                socketChannels.add(socketChannel);
                System.out.println("socketChannels size：" + socketChannels.size());
            }
        }
    }
}
