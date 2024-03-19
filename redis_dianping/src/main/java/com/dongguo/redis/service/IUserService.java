package com.dongguo.redis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongguo.redis.entity.POJO.User;
import jakarta.servlet.http.HttpSession;


/**
 * <p>
 *  服务类
 * </p>
 */
public interface IUserService extends IService<User> {

    String sendCode(String phone, HttpSession session);

//    Result login(LoginFormBO loginForm, HttpSession session);
//
//    Result logout(HttpServletRequest request);
//
//    Result sign();
//
//    Result signCount();

}
