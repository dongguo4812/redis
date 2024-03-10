package com.dongguo.redis.controller;

import com.dongguo.redis.service.GameSignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gameSign")
@Tag(
        name = "GameSignController",
        description = "游戏签到控制器接口")
public class GameSignController {
    @Resource
    private GameSignService gameSignService;

    @Operation(
            summary = "addGamer",
            description = "初始化数据"
    )
    @PostMapping("/addGamer")
    public void addGamer() {
        gameSignService.addGamer();
    }

    @Operation(
            summary = "getSignThreeDayCount",
            description = "连续三天签到的玩家"
    )
    @PostMapping("/getSignThreeDayCount")
    public Long getSignThreeDayCount() {
        return gameSignService.getSignThreeDayCount();
    }
}
