package com.dongguo.redis.controller;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

import static com.dongguo.redis.util.CacheConstants.CACHE_KEY_REDISSON_LOCK;

/**
 * @Author: Administrator
 * @Date: 2024-02-27
 */
public class RedissonLockDemo {
    private static Config config;
    private static Redisson redisson;
    static {
        config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setDatabase(0);
        redisson = (Redisson) Redisson.create(config);
    }
    public static void main(String[] args) {
        RLock lock = redisson.getLock(CACHE_KEY_REDISSON_LOCK);
        boolean isLocked = lock.tryLock();
        if (isLocked){
            System.out.println("加锁成功");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }
}
