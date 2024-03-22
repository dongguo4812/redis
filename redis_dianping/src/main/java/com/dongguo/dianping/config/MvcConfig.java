package com.dongguo.dianping.config;

import com.dongguo.dianping.support.interceptor.LoginInterceptor;
import com.dongguo.dianping.support.interceptor.RefreshTokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // token刷新的拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(redisTemplate))
                .addPathPatterns("/**")
                .order(0);

        //登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                //排除不需要拦截的路径，视情况添加
                .excludePathPatterns(
                        "/v3/**",  //swagger也需要排除
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/shop/**", //店铺信息
                        "/voucher/**",  //优惠券信息
                        "/shop-type/**",    //店铺类型信息
                        "/blog/hot",    //查询热门博客
                        "/user/sendCode",  //发送短信验证码
                        "/user/login"  //登录
                ).order(1);
    }
}
