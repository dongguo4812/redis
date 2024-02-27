package com.dongguo.lock.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dongguo.lock.util.CacheConstants.CACHE_KEY_ORDER_STOCK;

/**
 * @Author: Administrator
 * @Date: 2024-02-27
 */
@RestController
@Slf4j
public class OrderController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Value("${server.port}")
    private String port;

    /**
     * 默认每次购买一件商品
     *
     * @return
     */
    @GetMapping("getOrderStock")
    public String getOrderStock() {
        String key = CACHE_KEY_ORDER_STOCK + 1001;
        String result = stringRedisTemplate.opsForValue().get(key);
        int stockNum = result == null ? 0 : Integer.parseInt(result);
        if (stockNum > 0) {
            int actualStockNum = stockNum - 1;
            stringRedisTemplate.opsForValue().set(key, actualStockNum + "");
            return "你已经成功秒杀商品，此时还剩余" + actualStockNum + "件商品" + ",请求端口号：" + port;
        }
        return "秒杀失败，商品库存不足，请求端口号：" + port;
    }
}
