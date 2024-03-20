package com.dongguo.redis.controller;

import com.dongguo.redis.entity.BO.LoginFormBO;
import com.dongguo.redis.entity.DTO.UserDTO;
import com.dongguo.redis.entity.Result;
import com.dongguo.redis.service.IUserService;
import com.dongguo.redis.support.threadlocal.UserThreadLocalCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Administrator
 * @Date: 2024-03-19
 */
@RestController
@RequestMapping("/user")
@Tag(
        name = "UserController",
        description = "用户控制器接口")
public class UserController {

    @Resource
    private IUserService userService;
    @Operation(
            summary = "sendCode",
            description = "发送手机验证码"
    )
    @PostMapping("/sendCode")
    public Result sendCode(@RequestParam(value = "phone") String phone, HttpSession session) {
        return userService.sendCode(phone, session);
    }

    /**
     * 登录功能  目前只实现手机+验证码登录即可
     *
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    @Operation(
            summary = "login",
            description = "登录功能"
    )
    public Result login(@RequestBody LoginFormBO loginForm, HttpSession session) {
        return userService.login(loginForm,session);
    }

    /**
     * 请求接口时，拦截器会将session中的用户信息放入到ThreadLocal中
     * 所以直接从ThreadLocal中获取user信息即可。
     * @return
     */
    @GetMapping("/me")
    @Operation(
            summary = "me",
            description = "获取user信息"
    )
    public Result me() {
        UserDTO user = UserThreadLocalCache.getUser();
        return Result.ok(user);
    }
}
