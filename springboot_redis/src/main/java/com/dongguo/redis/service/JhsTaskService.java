package com.dongguo.redis.service;

import cn.hutool.core.date.DateUtil;
import com.dongguo.redis.entity.Product;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.micrometer.core.instrument.binder.cache.GuavaCacheMetrics;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.dongguo.redis.util.CacheConstants.*;

/**
 * @Author: Administrator
 * @Date: 2024-02-27
 */
@Service
@Slf4j
public class JhsTaskService {
    @Resource
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void initJhs() {
        log.info("定时任务 淘宝聚划算功能获取商品列表 模拟=====" + DateUtil.now());
        new Thread(() -> {
            while (true) {
                List<Product> productList = getProducts();
                //删除缓存数据
                redisTemplate.delete(CACHE_KEY_JHS_B);
                //将最新获取到的聚划算商品缓存到redis
                redisTemplate.opsForList().leftPushAll(CACHE_KEY_JHS_B, productList, 20, TimeUnit.DAYS);

                //保证删除缓存B时，缓存A还能够命中,删除缓存A时，缓存B已经重建
                redisTemplate.delete(CACHE_KEY_JHS_A);
                redisTemplate.opsForList().leftPushAll(CACHE_KEY_JHS_A, productList, 15, TimeUnit.DAYS);
                //模拟定时。间隔1分钟执行一次
                log.info("定时任务 淘宝聚划算功能获取商品列表 已刷新");
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1").start();
    }


    /**
     * 模拟从数据库读取20件特价商品，用于加载到聚划算的页面中
     *
     * @return
     */
    public List<Product> getProducts() {
        List<Product> productList = new ArrayList<>(20);
        for (int i = 1; i <= 20; i++) {
            Random random = new Random();
            //模拟生成id，随着定时任务的执行商品数不断增加
            long id = random.nextLong(10000);
            Product product = new Product(id, "product" + id, "detail" + id);
            productList.add(product);
        }
        return productList;
    }
}
