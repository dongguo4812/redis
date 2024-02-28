package com.dongguo.redis.controller;

import com.dongguo.redis.util.ShortUrlUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Random;

import static com.dongguo.redis.util.CacheConstants.CACHE_KEY_SHORT_URL;

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
    private RedisTemplate redisTemplate;
    @Resource
    private HttpServletResponse response;
    @Operation(
            summary = "长连接生成短连接",
            description = "长连接生成短连接"
    )
    @GetMapping("/encode")
    public String encode(@RequestParam("longUrl") String longUrl){
        String[] keys = ShortUrlUtils.shortUrl(longUrl);
        //任意取出其中一个
        String shortUrl = keys[new Random().nextInt(4)];

        log.info("生成短连接{}", shortUrl);
        redisTemplate.opsForHash().put(CACHE_KEY_SHORT_URL, shortUrl, longUrl);
        return "https://dongguo.com/" + shortUrl;
    }
    @Operation(
            summary = "获取长连接",
            description = "获取长连接"
    )
    @GetMapping("/decode")
    public void decode(@RequestParam("shortUrl") String shortUrl){
        String longUrl = (String)redisTemplate.opsForHash().get(CACHE_KEY_SHORT_URL, shortUrl);
        log.info("跳转连接{}" + longUrl);
        try {
            response.sendRedirect(longUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
