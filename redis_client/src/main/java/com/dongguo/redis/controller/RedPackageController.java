package com.dongguo.redis.controller;

import com.dongguo.redis.service.RedPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redPackage")
@Tag(
        name = "RedPackageController",
        description = "红包控制器接口")
public class RedPackageController {
    @Resource
    private RedPackageService redPackageService;
    @Operation(
            summary = "send",
            description = "发红包",
            parameters = {
            @Parameter(name = "totalMoney", description = "红包总金额", required = true, example = "1"),
            @Parameter(name = "RedPackageCount", description = "红包数量", required = true, example = "1")
    }
    )
    @PostMapping("/send")
    public String findUser(@RequestParam("totalMoney") Long totalMoney, @RequestParam("RedPackageCount") Integer RedPackageCount) {
        return redPackageService.send(totalMoney, RedPackageCount);
    }
    @Operation(
            summary = "grab",
            description = "抢红包",
            parameters = {
                    @Parameter(name = "redPackageKey", description = "红包的key", required = true, example = "1"),
                    @Parameter(name = "userId", description = "用户ID", required = true, example = "1")
            }
    )
    @PostMapping("/grab")
    public String rob(@RequestParam("redPackageKey") String redPackageKey, @RequestParam("userId") String userId) {
        return redPackageService.grab(redPackageKey, userId);
    }
}
