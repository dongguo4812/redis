package com.dongguo.dianping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongguo.dianping.entity.BO.LoginFormBO;
import com.dongguo.dianping.entity.POJO.User;
import com.dongguo.dianping.entity.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


/**
 * <p>
 *  服务类
 * </p>
 */
public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormBO loginForm, HttpSession session);


    Result logout(HttpServletRequest request);

    Result sign();

    Result signCount();

}
