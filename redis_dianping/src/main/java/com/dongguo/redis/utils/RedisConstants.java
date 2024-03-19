package com.dongguo.redis.utils;

/**
 * @Author: Administrator
 * @Date: 2024-03-19
 * redis缓存相关常量
 */
public class RedisConstants {

    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 15L;

    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 30L;
}
