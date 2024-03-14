package com.dongguo.redis.service;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import com.dongguo.redis.myredis.DistributedLockFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_INVENTORY_KEY;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_INVENTORY_LOCK_KEY;
import static com.dongguo.redis.utils.CommonConst.REDIS;

/**
 * @Author: Administrator
 * @Date: 2024-03-12
 */
@Service
@Slf4j
public class InventoryService {
    @Value(value = "${server.port}")
    private Integer port;
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DistributedLockFactory distributedLockFactory;

    public void initInventory() {
        //初始化库存100
        redisTemplate.opsForValue().set(CACHE_INVENTORY_KEY, 100);
    }

    /**
     * 单机锁解决超卖问题，分布式高并发场景下出现超卖现场
     *
     * @return
     */
    public synchronized String saleTicket() {

        //查询库存信息
        Object obj = redisTemplate.opsForValue().get(CACHE_INVENTORY_KEY);
        if (null != obj) {
            int inventory = (Integer) obj;
            //判断库存是否足够
            if (inventory > 0) {
                //扣减库存，减1
                inventory -= 1;
                redisTemplate.opsForValue().set(CACHE_INVENTORY_KEY, inventory);
                log.info("端口号：{} 售出一张票，还剩下{}张票", port, inventory);
                return "端口号：" + port + " 售出一张票，还剩下" + inventory + "张票";
            }
        }
        return "端口号：" + port + " 售票失败，库存为0";
    }

    /**
     * redis分布式锁解决分布式场景超卖问题
     * 递归调用存在栈溢出风险
     *
     * @return
     */
    public String saleTicketV2() {
        String key = CACHE_INVENTORY_KEY;
        String lockKey = CACHE_INVENTORY_LOCK_KEY;
        String value = UUID.fastUUID() + ":" + Thread.currentThread().getId();
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(lockKey, value);
        while (!absent) {
            //获取不到锁，进行重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return saleTicketV2();
        }
        try {
            //查询库存信息
            Object obj = redisTemplate.opsForValue().get(key);
            if (null != obj) {
                int inventory = (Integer) obj;
                //判断库存是否足够
                if (inventory > 0) {
                    //扣减库存，减1
                    inventory -= 1;
                    redisTemplate.opsForValue().set(key, inventory);
                    log.info("端口号：{} 售出一张票，还剩下{}张票", port, inventory);
                    return "端口号：" + port + " 售出一张票，还剩下" + inventory + "张票";
                }
            }
        } finally {
            redisTemplate.delete(lockKey);
        }
        return "端口号：" + port + " 售票失败，库存为0";
    }

    /**
     * 自旋代替递归
     *
     * @return
     */
    public String saleTicketV3() {
        String key = CACHE_INVENTORY_KEY;
        String lockKey = CACHE_INVENTORY_LOCK_KEY;
        String value = UUID.fastUUID() + ":" + Thread.currentThread().getId();
        //获取不到锁，自旋
        while (!redisTemplate.opsForValue().setIfAbsent(lockKey, value)) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            //查询库存信息
            Object obj = redisTemplate.opsForValue().get(key);
            if (null != obj) {
                int inventory = (Integer) obj;
                //判断库存是否足够
                if (inventory > 0) {
                    //扣减库存，减1
                    inventory -= 1;
                    redisTemplate.opsForValue().set(key, inventory);
                    log.info("端口号：{} 售出一张票，还剩下{}张票", port, inventory);
                    return "端口号：" + port + " 售出一张票，还剩下" + inventory + "张票";
                }
            }
        } finally {
            redisTemplate.delete(lockKey);
        }
        return "端口号：" + port + " 售票失败，库存为0";
    }

    /**
     * 分布式锁设置过期时间
     * 注意加锁和设置过期时间要保证原子性
     *
     * @return
     */
    public String saleTicketV4() {
        String key = CACHE_INVENTORY_KEY;
        String lockKey = CACHE_INVENTORY_LOCK_KEY;
        String value = UUID.fastUUID() + ":" + Thread.currentThread().getId();
        //获取不到锁，自旋
        while (!redisTemplate.opsForValue().setIfAbsent(lockKey, value, 30, TimeUnit.SECONDS)) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            //查询库存信息
            Object obj = redisTemplate.opsForValue().get(key);
            if (null != obj) {
                int inventory = (Integer) obj;
                //判断库存是否足够
                if (inventory > 0) {
                    //扣减库存，减1
                    inventory -= 1;
                    redisTemplate.opsForValue().set(key, inventory);
                    log.info("端口号：{} 售出一张票，还剩下{}张票", port, inventory);
                    return "端口号：" + port + " 售出一张票，还剩下" + inventory + "张票";
                }
            }
        } finally {
            redisTemplate.delete(lockKey);
        }
        return "端口号：" + port + " 售票失败，库存为0";
    }

    /**
     * 判断是否是自己加的锁，再释放锁
     *
     * @return
     */
    public String saleTicketV5() {
        String key = CACHE_INVENTORY_KEY;
        String lockKey = CACHE_INVENTORY_LOCK_KEY;
        String value = UUID.fastUUID() + ":" + Thread.currentThread().getId();
        //获取不到锁，自旋
        while (!redisTemplate.opsForValue().setIfAbsent(lockKey, value, 30, TimeUnit.SECONDS)) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            //查询库存信息
            Object obj = redisTemplate.opsForValue().get(key);
            if (null != obj) {
                int inventory = (Integer) obj;
                //判断库存是否足够
                if (inventory > 0) {
                    //扣减库存，减1
                    inventory -= 1;
                    redisTemplate.opsForValue().set(key, inventory);
                    log.info("端口号：{} 售出一张票，还剩下{}张票", port, inventory);
                    return "端口号：" + port + " 售出一张票，还剩下" + inventory + "张票";
                }
            }
        } finally {
            //判断是自己上的锁，才释放锁
            if (value.equals(redisTemplate.opsForValue().get(key))) {
                redisTemplate.delete(lockKey);
            }
        }
        return "端口号：" + port + " 售票失败，库存为0";
    }

    /**
     * lua脚本实现判断是否是自己上的锁和删除锁是一个原子操作。
     *
     * @return
     */
    public String saleTicketV6() {
        String key = CACHE_INVENTORY_KEY;
        String lockKey = CACHE_INVENTORY_LOCK_KEY;
        String value = IdUtil.fastUUID() + ":" + Thread.currentThread().getId();
        //获取不到锁，自旋
        while (!redisTemplate.opsForValue().setIfAbsent(lockKey, value, 30, TimeUnit.SECONDS)) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            //查询库存信息
            Object obj = redisTemplate.opsForValue().get(key);
            if (null != obj) {
                int inventory = (Integer) obj;
                //判断库存是否足够
                if (inventory > 0) {
                    //扣减库存，减1
                    inventory -= 1;
                    redisTemplate.opsForValue().set(key, inventory);
                    log.info("端口号：{} 售出一张票，还剩下{}张票", port, inventory);
                    return "端口号：" + port + " 售出一张票，还剩下" + inventory + "张票";
                }
            }
        } finally {
            String luaScript =
                    "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                            "return redis.call('del',KEYS[1]) " +
                            "else " +
                            "return 0 " +
                            "end";
            redisTemplate.execute(new DefaultRedisScript(luaScript, Boolean.class), Collections.singletonList(lockKey), value);
        }
        return "端口号：" + port + " 售票失败，库存为0";
    }

    /**
     * lua脚本实现分布式锁可重入性
     * @return
     */
    public String saleTicketV7() {
        String key = CACHE_INVENTORY_KEY;
        Lock redisLock = distributedLockFactory.getDistributedLock(REDIS);
        redisLock.lock();
        try {

            //查询库存信息
            Object obj = redisTemplate.opsForValue().get(key);
            if (null != obj) {
                int inventory = (Integer) obj;
                //判断库存是否足够
                if (inventory > 0) {
                    //扣减库存，减1
                    inventory -= 1;
                    redisTemplate.opsForValue().set(key, inventory);
                    reEntryTest();

                    log.info("端口号：{} 售出一张票，还剩下{}张票", port, inventory);
                    return "端口号：" + port + " 售出一张票，还剩下" + inventory + "张票";
                }
            }
        } finally {
            redisLock.unlock();
        }
        return "端口号：" + port + " 售票失败，库存为0";
    }

    private void reEntryTest() {
        Lock redisLock = distributedLockFactory.getDistributedLock(REDIS);
        redisLock.lock();
        try {
            log.info("==============测试可重入性===============");
        } finally {
            redisLock.unlock();
        }
    }

    /**
     * 实现自动续期
     * @return
     */
    public String saleTicketV8() {
        String key = CACHE_INVENTORY_KEY;
        Lock redisLock = distributedLockFactory.getDistributedLock(REDIS);
        redisLock.lock();
        try {
            //查询库存信息
            Object obj = redisTemplate.opsForValue().get(key);
            if (null != obj) {
                int inventory = (Integer) obj;
                //判断库存是否足够
                if (inventory > 0) {
                    //扣减库存，减1
                    inventory -= 1;
                    redisTemplate.opsForValue().set(key, inventory);
                    //业务执行时间超过超时时间
                    try {
                        TimeUnit.SECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("端口号：{} 售出一张票，还剩下{}张票", port, inventory);
                    return "端口号：" + port + " 售出一张票，还剩下" + inventory + "张票";
                }
            }
        } finally {
            redisLock.unlock();
        }
        return "端口号：" + port + " 售票失败，库存为0";
    }
}
