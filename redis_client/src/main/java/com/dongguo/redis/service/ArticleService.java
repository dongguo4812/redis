package com.dongguo.redis.service;

import cn.hutool.core.date.DateUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_ARTICLE_KEY;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_WECHAT_KEY;

@Slf4j
@Service
public class ArticleService {
    @Resource
    private RedisTemplate redisTemplate;

    public void addReading(String articleId) {
        String key = CACHE_ARTICLE_KEY + articleId;
        Long number = redisTemplate.opsForValue().increment(key);
        log.info("文章编号:{}, 阅读量:{}", articleId, number);
    }

    public Integer getReading(String articleId) {
        String key = CACHE_ARTICLE_KEY + articleId;
        Object obj = redisTemplate.opsForValue().get(key);
        log.info("文章编号:{}, 阅读量:{}", articleId, obj == null ? 0 : obj);
        return (Integer) obj;
    }

    public void addReadingOnlyOnce(String articleId, String weChatId) {
        String key = CACHE_ARTICLE_KEY + articleId;
        String weChatKey = CACHE_WECHAT_KEY + articleId + ":" + weChatId;
        Boolean member = redisTemplate.opsForSet().isMember(weChatKey, weChatId);
        if (Boolean.FALSE.equals(member)) {
            Long number = redisTemplate.opsForValue().increment(key);
            log.info("文章编号:{}, 阅读量:{}", articleId, number);
            redisTemplate.opsForSet().add(weChatKey, weChatId);
        }
    }

    public void addReadingOnlyOnceOfDay(String articleId, String weChatId) {
        String key = CACHE_ARTICLE_KEY + articleId;
        String weChatKey = CACHE_WECHAT_KEY + articleId + ":" +  weChatId;
        Boolean hasKey = redisTemplate.hasKey(weChatKey);
        Boolean member = redisTemplate.opsForSet().isMember(weChatKey, weChatId);
        if (Boolean.FALSE.equals(member)) {
            Long number = redisTemplate.opsForValue().increment(key);
            log.info("文章编号:{}, 阅读量:{}", articleId, number);
            redisTemplate.opsForSet().add(weChatKey, weChatId);
            //当该key第一次赋值时设置过期时间为明天0点,可以使用定时任务
            if (Boolean.FALSE.equals(hasKey)) {
                // 获取当前日期
                Date today = new Date();
                // 获取明天的日期
                Date tomorrow = DateUtil.offsetDay(today, 1);
                // 获取明天凌晨的日期时间（0点）
                Date tomorrowMidnight = DateUtil.beginOfDay(tomorrow);
                redisTemplate.expireAt(weChatKey, tomorrowMidnight);
            }
        }
    }
}
