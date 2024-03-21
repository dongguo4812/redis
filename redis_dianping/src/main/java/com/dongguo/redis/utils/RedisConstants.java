package com.dongguo.redis.utils;

/**
 * @Author: Administrator
 * @Date: 2024-03-19
 * redis缓存相关常量
 */
public class RedisConstants {
    /**
     * user
     */
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 15L;

    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 30L;
    /**
     * shop
     */
    public static final Long CACHE_SHOP_TTL = 30L;
    public static final String CACHE_SHOP_KEY = "cache:shop:";

    public static final String CACHE_SHOP_TYPE_KEY = "cache:shop_type:list";
    public static final String LOCK_SHOP_KEY = "lock:shop:";
    public static final Long LOCK_SHOP_TTL = 10L;



    public static final Long CACHE_NULL_TTL = 2L;


    public static final String CACHE_SNOWFLAKE_WORKID_key = "application:snowflake:workid";
}
