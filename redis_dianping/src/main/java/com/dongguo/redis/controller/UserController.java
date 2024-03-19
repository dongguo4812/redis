package com.dongguo.redis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @Operation(
            summary = "根据Id查询用户信息",
            description = "根据ID查询用户信息，并返回响应结果信息"
    )
    @GetMapping("/hello")
    public void hello() {
        System.out.println("hello");
    }
}
