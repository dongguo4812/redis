package com.dongguo.redis.service;

import cn.hutool.core.util.ObjectUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.dongguo.redis.util.CacheConstants.CACHE_KEY_HYPELOGLOG;

/**
 * @Author: Administrator
 * @Date: 2024-02-26
 */
@Service
@Slf4j
public class HypeLogLogService {

    @Resource
    private RedisTemplate redisTemplate;

    public Long getUV(){
        String key = CACHE_KEY_HYPELOGLOG;
        //自动值去重
        Long UV = redisTemplate.opsForHyperLogLog().size(key);
        if (ObjectUtil.isNull(UV)){
            return 0L;
        }
        return UV;
    }

    public void addUV(Long userId) {
        String key = CACHE_KEY_HYPELOGLOG;
        //PFCOUNT
        redisTemplate.opsForHyperLogLog().add(key, userId);
    }
}
