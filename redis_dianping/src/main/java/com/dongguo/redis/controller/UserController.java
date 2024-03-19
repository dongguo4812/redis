package com.dongguo.redis.controller;

import com.dongguo.redis.service.IUserService;
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
    @GetMapping("/sendCode")
    public String sendCode(@RequestParam(value = "phone") String phone, HttpSession session) {
        return userService.sendCode(phone, session);
    }
}
