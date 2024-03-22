package com.dongguo.dianping.support.threadlocal;


import com.dongguo.dianping.entity.DTO.UserDTO;

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
