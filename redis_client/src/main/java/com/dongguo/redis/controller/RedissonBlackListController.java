package com.dongguo.redis.controller;

import com.dongguo.redis.service.RedissonBlackListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redissonBalckList")
@Tag(
        name = "RedissonBlackListController",
        description = "Redisson黑名单控制器接口")
public class RedissonBlackListController {

    @Resource
    private RedissonBlackListService redissonBlackListService;

    @Operation(
            summary = "/videoRecommendation",
            description = "视频推荐"
    )
    @PostMapping("/videoRecommendation”/{videoId}")
    public String login(@PathVariable("videoId") String videoId) {
        return redissonBlackListService.videoRecommendation(videoId);
    }
}
