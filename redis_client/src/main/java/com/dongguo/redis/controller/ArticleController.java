package com.dongguo.redis.controller;

import com.dongguo.redis.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
@Tag(
        name = "ArticleController",
        description = "文章控制器接口")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Operation(
            summary = "需求1.0:增加阅读量",
            description = "用户阅读文章贡献一个阅读量"
    )
    @PostMapping("/addReading/{articleId}")
    public void addReading(@PathVariable(value = "articleId") String articleId) {
        articleService.addReading(articleId);
    }

    @Operation(
            summary = "获取阅读量",
            description = "获取阅读量"
    )
    @PostMapping("/getReading/{articleId}")
    public Integer getReading(@PathVariable(value = "articleId") String articleId) {
        return articleService.getReading(articleId);
    }

    @Operation(
            summary = "需求2.0:增加阅读量",
            description = "同篇文章一个微信号只能贡献一个阅读量"
    )
    @PostMapping("/addReadingOnlyOnce")
    public void addReadingOnlyOnce(@RequestParam(value = "articleId") String articleId, @RequestParam(value = "weChatId") String weChatId) {
        articleService.addReadingOnlyOnce(articleId, weChatId);
    }

    @Operation(
            summary = "需求3.0:增加阅读量",
            description = "同篇文章一个微信号每天只能贡献一次阅读量"
    )
    @PostMapping("/addReadingOnlyOnceOfDay")
    public void addReadingOnlyOnceOfDay(@RequestParam(value = "articleId") String articleId, @RequestParam(value = "weChatId") String weChatId) {
        articleService.addReadingOnlyOnceOfDay(articleId, weChatId);
    }
}
