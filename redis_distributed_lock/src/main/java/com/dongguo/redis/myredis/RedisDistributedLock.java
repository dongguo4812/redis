package com.dongguo.redis.myredis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Redis分布式锁
 * lua脚本实现分布式锁的可重入性，实现Lock接口
 */
@Slf4j
public class RedisDistributedLock implements Lock {

    private RedisTemplate redisTemplate;
    private String key;
    private String value;
    private Long expire;

    /**
     * 默认过期时间30秒
     *
     * @param redisTemplate
     * @param key
     * @param uuid
     */
    public RedisDistributedLock(RedisTemplate redisTemplate, String key, String uuid) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        //同一个实例uuid相同，不同线程对应不同的ThreadId
        this.value = uuid + ":" + Thread.currentThread().getId();
        this.expire = 30L;
    }

    /**
     * 加锁方法
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
        return tryLock(expire, TimeUnit.SECONDS);

    }

    /**
     * 真正调用的加锁方法
     *
     * @param time the maximum time to wait for the lock
     * @param unit the time unit of the {@code time} argument
     * @return
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        //将过期时间转换为秒
        this.expire = unit.toSeconds(time);
        String script =
                "if redis.call('exists', KEYS[1]) == 0 or redis.call('hexists', KEYS[1], ARGV[1]) == 1 then  " +
                        "    redis.call('hincrby', KEYS[1], ARGV[1], 1)  " +
                        "    redis.call('expire', KEYS[1], ARGV[2])  " +
                        "    return 1  " +
                        "else  " +
                        "    return 0  " +
                        "end";
        while (!(Boolean) redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Collections.singletonList(key), value, expire)) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("lock()   key:{}, value:{}", key, value);

        //新建一个后台扫描程序，监测key的过期时间，每过expire/3,实现续期expire
        reExpire();
        return true;
    }

    private void reExpire() {
        String script =
                "if redis.call('HEXISTS', KEYS[1], ARGV[1]) == 1 then  " +
                        "    return redis.call('expire', KEYS[1], ARGV[2])  " +
                        "else  " +
                        "    return 0  " +
                        "end";
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if ((Boolean) redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Collections.singletonList(key), value, expire)) {
                    log.info("自动续期  key:{}, value:{}", key, value);
                    reExpire();
                }
                //key不存在，退出定时任务
                cancel();
            }
            //延迟时间 毫秒
        }, (this.expire * 1000) / 3);
    }

    /**
     * 解锁方法
     */
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
        Object flag = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(key), value);
        /**
         * 即返回nil
         */
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
