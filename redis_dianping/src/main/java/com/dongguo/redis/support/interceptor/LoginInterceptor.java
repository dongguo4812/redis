package com.dongguo.redis.support.interceptor;

import com.dongguo.redis.support.threadlocal.UserThreadLocalCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * 登录拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (UserThreadLocalCache.getUser() == null){
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
