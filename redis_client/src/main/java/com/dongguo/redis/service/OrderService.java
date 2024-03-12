package com.dongguo.redis.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_ORDER_KEY;

@Service
@Slf4j
public class OrderService {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    public void addOrder() {
        Long orderId = ThreadLocalRandom.current().nextLong(1000) + 1;
        String orderNO = UUID.randomUUID().toString();
        String key = CACHE_ORDER_KEY + orderId;
        String value = "订单号:" + orderNO;
        redisTemplate.opsForValue().set(key, value);
        log.info("新增订单，订单id:{}，订单No:{}", key, orderNO);
    }

    public String getOrder(Long orderId) {
        String key = CACHE_ORDER_KEY + orderId;
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            log.info("订单不存在");
        } else {
            log.info((String) obj);
        }
        return (String) obj;
    }
}
