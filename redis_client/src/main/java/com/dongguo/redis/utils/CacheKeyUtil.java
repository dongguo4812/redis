package com.dongguo.redis.utils;

/**
 * 缓存key
 */
public class CacheKeyUtil {

    public static final String CACHE_ORDER_KEY = "redis:order:";
    public  static final String CACHE_USER_KEY="redis:user:";
    public  static final String CACHE_USER_LOCK_KEY="redis:user:lock:";
    public  static final String CACHE_USER_BLOOMFILTER_KEY="redis:user:bloomFilter:";
    public  static final String CACHE_ARTICLE_KEY="redis:article:";
    public  static final String CACHE_WECHAT_KEY="redis:wechat:";
    public  static final String CACHE_TMALL_UV_KEY="redis:tmall:uv";
    public  static final String CACHE_HOTEL_KEY="redis:hotel";
    public  static final String CACHE_GAME_SIGN_KEY="redis:game:sign:";

    public  static final String CACHE_WHITE_LIST_KEY="redis:white:list";
    public  static final String CACHE_JHS_KEY="redis:jhs";
    public  static final String CACHE_JHS_A_KEY="redis:jhs:a";
    public  static final String CACHE_JHS_B_KEY="redis:jhs:b";
    public  static final String CACHE_RED_PACKAGE_KEY="redis:redPackage:";
    public  static final String CACHE_RED_PACKAGE_CONSUME_KEY="redis:redPackage:user:";

    public  static final String CACHE_SHORT_URL_KEY="redis:short:url";
}
