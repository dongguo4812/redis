package com.dongguo.redis.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_INVENTORY_KEY;

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

    public synchronized String saleTicket() {
        String message;
        Object obj = redisTemplate.opsForValue().get(CACHE_INVENTORY_KEY);
        if (null != obj) {
            Integer inventory = (Integer) obj;
            if (inventory > 0) {
                inventory -= 1;
                redisTemplate.opsForValue().set(CACHE_INVENTORY_KEY, inventory);
                log.info("端口号：{} 售出一张票，还剩下{}张票", port, inventory);
                message = "端口号：" + port + " 售出一张票，还剩下" + inventory + "张票";
                return message;
            }
        }
        return "端口号：" + port + " 售票失败，库存为0";
    }
}
