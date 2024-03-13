package com.dongguo.redis.service;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_INVENTORY_KEY;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_INVENTORY_LOCK_KEY;

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
     * @return
     */
    public String saleTicketV2() {
        String key = CACHE_INVENTORY_KEY;
        String lockKey = CACHE_INVENTORY_LOCK_KEY;
        String value = UUID.fastUUID() + ":" + Thread.currentThread().getId();
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(lockKey, value);
        if (!absent) {
            //获取不到锁，进行重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            saleTicketV2();
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
}
