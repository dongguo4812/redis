package com.dongguo.redis.controller;


import com.dongguo.redis.service.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Administrator
 * @Date: 2024-02-28
 */
@RestController
@RequestMapping("/shortUrl")
@Tag(
        name = "ShortUrlController",
        description = "短连接控制器接口")
@Slf4j
public class ShortUrlController {
    @Resource
    private ShortUrlService shortUrlService;

    @Operation(
            summary = "encode",
            description = "长连接生成短连接"
    )
    @GetMapping("/encode")
    public String encode(@RequestParam("longUrl") String longUrl) {
        return shortUrlService.encode(longUrl);
    }

    @Operation(
            summary = "decode",
            description = "获取长连接"
    )
    @GetMapping("/decode")
    public void decode(HttpServletResponse response, @RequestParam("shortUrl") String shortUrl) {
        shortUrlService.decode(response, shortUrl);
    }
}
