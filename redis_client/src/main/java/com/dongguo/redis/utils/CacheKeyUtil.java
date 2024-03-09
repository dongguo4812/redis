package com.dongguo.redis.utils;

/**
 * 缓存key
 */
public class CacheKeyUtil {

    public static final String ORDER_KEY = "order:";
    public  static final String CACHE_USER_KEY="redis:user:";
    public  static final String CACHE_USER_LOCK_KEY="redis:user:lock:";
    public  static final String CACHE_ARTICLE_KEY="redis:article:";
    public  static final String CACHE_HYPELOGLOG_KEY="redis:hypeLogLog";
    public  static final String CACHE_GEO_KEY="redis:geo";
    public  static final String CACHE_JHS_KEY="redis:jhs";
    public  static final String CACHE_JHS_A_KEY="redis:jhs:a";
    public  static final String CACHE_JHS_B_KEY="redis:jhs:b";
    public  static final String CACHE_REDISSON_LOCK_KEY="redisson:lock";
    public  static final String CACHE_SHORT_URL_KEY="redis:short:url";
}
