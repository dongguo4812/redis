package com.dongguo.redis.support.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dongguo.redis.entity.DTO.UserDTO;
import com.dongguo.redis.support.threadlocal.UserThreadLocalCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

import static com.dongguo.redis.utils.RedisConstants.LOGIN_USER_KEY;


/**
 * 登录拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {
    private StringRedisTemplate stringRedisTemplate;

    public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
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

        String token = request.getHeader("authorization");
        if (token == null){
            //不存在，即未进行登录，进行拦截
            return false;
        }
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY + token);
        if (ObjectUtil.isEmpty(userMap)){
            return false;
        }
        UserDTO userDTO = BeanUtil.mapToBean(userMap, UserDTO.class,true);
        if (userDTO == null){
            //不存在，即未进行登录，进行拦截
            return false;
        }
        //存在，保存用户信息到threadLocal
        UserThreadLocalCache.setUser(userDTO);
        //放行
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
        // 移除ThreadLocal用户信息
        UserThreadLocalCache.removeUser();
    }
}
