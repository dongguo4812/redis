package com.dongguo.redis.support.threadlocal;


import com.dongguo.redis.entity.DTO.UserDTO;

/**
 * user ThreadLocalCache
 */
public class UserThreadLocalCache {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void setUser(UserDTO user){
        tl.set(user);
    }

    public static UserDTO getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
