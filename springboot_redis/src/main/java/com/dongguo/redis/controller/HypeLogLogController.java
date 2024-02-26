package com.dongguo.redis.controller;

import com.dongguo.redis.service.HypeLogLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Administrator
 * @Date: 2024-02-26
 */
@RestController
@RequestMapping("/hypeLogLog")
@Tag(
        name = "HypeLogLogController",
        description = "统计控制器接口")
public class HypeLogLogController {

    @Resource
    private HypeLogLogService hypeLogLogService;

    @Operation(
            summary = "新增UV",
            description = "新增UV"
    )
    @PostMapping("/addUV/{userId}")
    public void addUV(@PathVariable Long userId) {
        hypeLogLogService.addUV(userId);
    }

    @Operation(
            summary = "获取UV",
            description = "获取UV"
    )
    @GetMapping("/getUV")
    public Long getUV() {
        return hypeLogLogService.getUV();
    }
}
