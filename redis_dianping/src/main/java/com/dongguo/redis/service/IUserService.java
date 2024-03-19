package com.dongguo.redis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongguo.redis.entity.BO.LoginFormBO;
import com.dongguo.redis.entity.POJO.User;
import com.dongguo.redis.entity.Result;
import jakarta.servlet.http.HttpSession;


/**
 * <p>
 *  服务类
 * </p>
 */
public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormBO loginForm, HttpSession session);

//
//    Result logout(HttpServletRequest request);
//
//    Result sign();
//
//    Result signCount();

}
