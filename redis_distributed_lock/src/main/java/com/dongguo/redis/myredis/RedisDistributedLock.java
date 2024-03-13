package com.dongguo.redis.myredis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Redis分布式锁
 * lua脚本实现分布式锁的可重入性，实现Lock接口
 */
@Slf4j
public class RedisDistributedLock implements Lock {

    private StringRedisTemplate redisTemplate;
    private String key;
    private String value;
    private Long expire;

    public RedisDistributedLock(StringRedisTemplate redisTemplate, String key, String uuid) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.value = uuid + ":" + Thread.currentThread().getId();
        this.expire = 30L;
    }

    /**
     * 阻塞方法。如果当前线程未持有锁，它会一直等待，直到获取到锁为止。
     * 这里简化lock方法直接调用tryLock方法
     */
    @Override
    public void lock() {
        tryLock();
    }

    /**
     * 非阻塞方法。如果当前线程能够立即获取到锁，则返回true；如果锁被其他线程持有，则立即返回false
     *
     * @return
     */
    @Override
    public boolean tryLock() {
        String script =
                "if redis.call('exists', KEYS[1]) == 0 or redis.call('hexists', KEYS[1], ARGV[1]) == 1 then  " +
                        "    redis.call('hincrby', KEYS[1], ARGV[1], 1)  " +
                        "    redis.call('expire', KEYS[1], ARGV[2])  " +
                        "    return 1  " +
                        "else  " +
                        "    return 0  " +
                        "end";
        while (!redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(key), value, String.valueOf(expire))) {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("lock()   key:{}, value:{}", key, value);
        return true;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        String script =
                "if redis.call('HEXISTS', KEYS[1], ARGV[1]) == 0 then  " +
                        "    return nil  " +
                        "elseif redis.call('HINCRBY', KEYS[1], ARGV[1], -1) == 0 then  " +
                        "    return redis.call('del', KEYS[1])  " +
                        "else  " +
                        "    return 0  " +
                        "end";
        log.info("unlock()   key:{}, value:{}", key, value);
        Object flag = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(key), value);
        if (null == flag) {
            throw new RuntimeException("this lock doesn't exists!");
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
