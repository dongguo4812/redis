package com.dongguo.redis.controller;

import com.dongguo.redis.service.GuavaWhiteListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Administrator
 * @Date: 2024-03-11
 */
@RestController
@RequestMapping("/guavaWhiteList")
@Tag(
        name = "GuavaWhiteListController",
        description = "Guava实现白名单控制器接口")
public class GuavaWhiteListController {
    @Resource
    private GuavaWhiteListService guavaWhiteListService;

    @Operation(
            summary = "initWhiteList",
            description = "初始化白名单"
    )
    @PostMapping("/initWhiteList")
    public void initWhiteList() {
        guavaWhiteListService.initWhiteList();
    }

    @Operation(
            summary = "/login",
            description = "用户登录"
    )
    @PostMapping("/login/{ip}")
    public String login(@PathVariable("ip") String ip) {
       return guavaWhiteListService.login(ip);
    }
}
