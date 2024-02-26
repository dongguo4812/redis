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
            summary = "增加喜欢数",
            description = "增加喜欢数"
    )
    @PostMapping("/likeArticle/{articleId}")
    public void likeArticle(@PathVariable String articleId) {
        articleService.likeArticle(articleId);
    }

    @Operation(
            summary = "获取喜欢数",
            description = "获取喜欢数"
    )
    @GetMapping("/getLikeNumber/{articleId}")
    public Long getLikeNumber(@PathVariable String articleId) {
       return articleService.getLikeNumber(articleId);
    }
}
