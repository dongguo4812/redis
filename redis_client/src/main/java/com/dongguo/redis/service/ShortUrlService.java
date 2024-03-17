package com.dongguo.redis.service;

import com.dongguo.redis.utils.ShortUrlUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Random;

import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_SHORT_URL_KEY;

@Service
@Slf4j
public class ShortUrlService {
    @Resource
    private RedisTemplate redisTemplate;

    public String encode(String longUrl) {
        String[] keys = ShortUrlUtils.shortUrl(longUrl);
        //任意取出其中一个
        String shortUrl = keys[new Random().nextInt(4)];

        log.info("生成短连接{}", shortUrl);
        redisTemplate.opsForHash().put(CACHE_SHORT_URL_KEY, shortUrl, longUrl);
        return shortUrl;
    }


    public void decode(HttpServletResponse response, String shortUrl) {
        String longUrl = (String) redisTemplate.opsForHash().get(CACHE_SHORT_URL_KEY, shortUrl);
        log.info("跳转连接{}" + longUrl);
        try {
            response.sendRedirect(longUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
