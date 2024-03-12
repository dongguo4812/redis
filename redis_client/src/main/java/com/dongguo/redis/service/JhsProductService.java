package com.dongguo.redis.service;

import cn.hutool.core.collection.CollUtil;
import com.dongguo.redis.entity.Product;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.dongguo.redis.utils.CacheKeyUtil.*;


/**
 * @Author: Administrator
 * @Date: 2024-02-27
 */
@Service
@Slf4j
public class JhsProductService {

    @Resource
    private RedisTemplate redisTemplate;
    @Autowired
    private JhsTaskService jhsTaskService;

    public List<Product> findJhsProductPage(int page, int size) {
        List productList;
        long start = (page - 1) * size;
        long end = start + size - 1;
        try {
            //查缓存
            productList = redisTemplate.opsForList().range(CACHE_JHS_KEY, start, end);
            if (CollUtil.isEmpty(productList)) {
                //可能查到的列表为空，说明正在重建缓存列表。需要查询数据库获取参加聚划算最新的商品列表,并将商品列表缓存到redis中
                productList = jhsTaskService.getProducts();
                //将最新获取到的聚划算商品缓存到redis
                redisTemplate.opsForList().leftPushAll(CACHE_JHS_KEY, productList);
                //设置过期时间
                redisTemplate.expire(CACHE_JHS_KEY, 1, TimeUnit.DAYS);
            }
        } catch (Exception e) {
            log.info("查询redis缓存失败 exception:" + e.getMessage());
            //这里异常一般是redis瘫痪或网络超时， 查询数据库
            productList = jhsTaskService.getProducts();
        }
        return productList;
    }

    /**
     * 通过加锁的方式解决缓存击穿
     *
     * @param page
     * @param size
     * @return
     */
    public List<Product> findJhsProductPage2(int page, int size) {
        List productList;
        long start = (page - 1) * size;
        long end = start + size - 1;
        try {
            //查缓存
            productList = redisTemplate.opsForList().range(CACHE_JHS_KEY, start, end);
            if (CollUtil.isEmpty(productList)) {
                //双重校验锁
                synchronized (this) {
                    productList = redisTemplate.opsForList().range(CACHE_JHS_KEY, start, end);
                    if (CollUtil.isEmpty(productList)) {
                        //可能查到的列表为空，说明正在重建缓存列表。需要查询数据库获取参加聚划算最新的商品列表,并将商品列表缓存到redis中
                        productList = jhsTaskService.getProducts();
                        //将最新获取到的聚划算商品缓存到redis
                        redisTemplate.opsForList().leftPushAll(CACHE_JHS_KEY, productList);
                        //设置过期时间
                        redisTemplate.expire(CACHE_JHS_KEY, 1, TimeUnit.DAYS);
                    }
                }
            }
        } catch (Exception e) {
            log.info("查询redis缓存失败 exception:" + e.getMessage());
            //这里异常一般是redis瘫痪或网络超时， 查询数据库
            productList = jhsTaskService.getProducts();
        }
        return productList;
    }

    /**
     * 多缓存策略
     *
     * @param page
     * @param size
     * @return
     */
    public List<Product> findJhsProductPage3(int page, int size) {
        List productList;
        long start = (page - 1) * size;
        long end = start + size - 1;
        try {
            //查缓存A
            productList = redisTemplate.opsForList().range(CACHE_JHS_A_KEY, start, end);
            if (CollUtil.isEmpty(productList)) {
                //不存在 查缓存B
                productList = redisTemplate.opsForList().range(CACHE_JHS_B_KEY, start, end);
                //差异失效时间 缓存A为空，缓存B的缓存已经重建一定有数据，所以无需判断是否为空
                log.info("缓存B命中");
            } else {
                log.info("缓存A命中");
            }
        } catch (Exception e) {
            log.info("查询redis缓存失败 exception:" + e.getMessage());
            //这里异常一般是redis瘫痪或网络超时， 查询数据库
            productList = jhsTaskService.getProducts();
        }
        return productList;
    }
}
