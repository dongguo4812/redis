package com.dongguo.redis.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Random;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_TMALL_UV_KEY;

@Service
@Slf4j
public class TmallUniqueVisitorsService {
    @Resource
    private RedisTemplate redisTemplate;


    public Long getUV() {
        Long size = redisTemplate.opsForHyperLogLog().size(CACHE_TMALL_UV_KEY);
        log.info("当前UV: {}", size);
        return size;
    }

    public void addUV() {
        String key = CACHE_TMALL_UV_KEY;
        Random random = new Random();
        //每次点击，模拟200个ip访问
        for (int i = 0; i < 200; i++) {
            String randomIp = random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
            //PFCOUNT
            redisTemplate.opsForHyperLogLog().add(key, randomIp);
            log.info("当前访问ip: {}", randomIp);
        }
    }
}
