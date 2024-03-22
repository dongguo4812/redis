package com.dongguo.redis.utils;

public class SystemConstants {
    public static final String IMAGE_UPLOAD_DIR = "E:\\software\\nginx\\nginx-1.18.0\\html\\dianping\\imgs\\";
    public static final String USER_NICK_NAME_PREFIX = "user_";
    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final int MAX_PAGE_SIZE = 10;

    /**
     * StrNum
     */
    public interface StrNum {
        String ZERO = "0";
        String ONE = "1";
        String TWO = "2";
        String THREE = "3";
        String FOUR = "4";
        String FIVE = "5";
        String SIX = "6";
        String TEN = "10";
        String HUNDRED = "100";
        String THOUSAND = "1000";
    }

    /**
     * IntegerNum
     */
    public interface IntegerNum {
        Integer ZERO = 0;
        Integer ONE = 1;
        Integer TWO = 2;
        Integer THREE = 3;
        Integer FOUR = 4;
        Integer FIVE = 5;
        Integer SIX = 6;
        Integer EIGHT = 8;
        Integer TEN = 10;
        Integer HUNDRED = 100;
    }
}
