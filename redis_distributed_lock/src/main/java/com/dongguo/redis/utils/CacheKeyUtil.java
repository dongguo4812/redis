package com.dongguo.redis.utils;

/**
 * @Author: Administrator
 * @Date: 2024-03-12
 */
public class CacheKeyUtil {
    public  static final String CACHE_INVENTORY_KEY="redis:inventory";
    public  static final String CACHE_INVENTORY_LOCK_KEY="redis:inventory:lock";
    public  static final String CACHE_REDISSON_LOCK_KEY="redisson:lock";
}
