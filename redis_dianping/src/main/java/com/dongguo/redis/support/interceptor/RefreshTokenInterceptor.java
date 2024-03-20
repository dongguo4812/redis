package com.dongguo.redis.support.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.dongguo.redis.entity.DTO.UserDTO;
import com.dongguo.redis.support.threadlocal.UserThreadLocalCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.dongguo.redis.utils.RedisConstants.LOGIN_USER_KEY;
import static com.dongguo.redis.utils.RedisConstants.LOGIN_USER_TTL;

public class RefreshTokenInterceptor implements HandlerInterceptor {

    private RedisTemplate redisTemplate;

    public RefreshTokenInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    /**
     * 在控制器方法（即处理请求的@RequestMapping注解的方法）之前调用
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
       /*     //获取session
        HttpSession session = request.getSession();
        //获取session中用户信息
        Object user = session.getAttribute("user");
        if (user == null){
            //不存在，即未进行登录，进行拦截
            return false;
        }
        //存在，保存用户信息到threadLocal
        UserThreadLocalCache.setUser((UserDTO)user);
        */
        // 1.获取请求头中的token
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }
        // 2.基于TOKEN获取redis中的用户
        String tokenKey  = LOGIN_USER_KEY + token;
        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(tokenKey);
        // 3.判断用户是否存在
        if (userMap.isEmpty()) {
            return true;
        }
        // 5.将查询到的hash数据转为UserDTO
        UserDTO userDTO = BeanUtil.mapToBean(userMap, UserDTO.class,false);
        // 6.存在，保存用户信息到 ThreadLocal
        UserThreadLocalCache.setUser(userDTO);
        // 7.刷新token有效期
        redisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);
        // 8.放行
        return true;
    }
    /**
     * 在整个请求处理完成之后调用
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserThreadLocalCache.removeUser();
    }
}
