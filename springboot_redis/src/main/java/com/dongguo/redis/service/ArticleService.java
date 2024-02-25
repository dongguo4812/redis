package com.dongguo.redis.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.dongguo.redis.util.CacheConstants.CACHE_KEY_ARTICLE;

@Slf4j
@Service
public class ArticleService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void likeArticle(String articleId) {
        String key = CACHE_KEY_ARTICLE + articleId;
        Long number = stringRedisTemplate.opsForValue().increment(key);
        log.info("文章编号:{}, 喜欢数:{}", articleId, number);

    }

    public Long getLikeNumber(String articleId) {
        String key = CACHE_KEY_ARTICLE + articleId;
        String number = stringRedisTemplate.opsForValue().get(key);
        log.info("文章编号:{}, 喜欢数:{}", articleId, number);
        return Long.valueOf(number);
    }
}
