package com.dongguo.redis.service;

import cn.hutool.core.collection.CollUtil;
import com.dongguo.redis.entity.Product;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dongguo.redis.util.CacheConstants.*;

/**
 * @Author: Administrator
 * @Date: 2024-02-27
 */
@Service
@Slf4j
public class JhsProductService {

    @Resource
    private RedisTemplate redisTemplate;

    public List<Product> findJhsProducts(int page, int size) {
        List productList = null;
        long start = (page - 1) * size;
        long end = start + size - 1;
        try {
            //查缓存A
            productList = redisTemplate.opsForList().range(CACHE_KEY_JHS_A, start, end);
            if (CollUtil.isEmpty(productList)){
                //不存在
                productList = redisTemplate.opsForList().range(CACHE_KEY_JHS_B, start, end);
                if (CollUtil.isEmpty(productList)){
                    //不存在查数据库
                }else {
                    System.out.println("缓存B 命中");
                }
            }else {
                System.out.println("缓存A 命中");
            }
        } catch (Exception e) {
            log.info("查询redis缓存失败 exception:" + e.getMessage());
            //这里异常一般是redis瘫痪或网络超时， 查询数据库
        }
        return productList;
    }
}
