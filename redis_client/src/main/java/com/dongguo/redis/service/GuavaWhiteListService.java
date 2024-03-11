package com.dongguo.redis.service;

import com.google.common.hash.BloomFilter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Random;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_WHITE_LIST_KEY;

/**
 * @Author: Administrator
 * @Date: 2024-03-11
 */
@Service
@Slf4j
public class GuavaWhiteListService {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private BloomFilter<String> bloomFilter;
//    @PostConstruct
    public void initWhiteList() {
        String key = CACHE_WHITE_LIST_KEY;
        Random random = new Random();
        //重置缓存
        redisTemplate.delete(key);
        //模拟200个ip的白名单
        for (int i = 0; i < 200; i++) {
            String randomIp = random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
            log.info("用户白名单加入到布隆过滤器， ip:{}", randomIp);
            bloomFilter.put(randomIp);
            redisTemplate.opsForSet().add(key, randomIp);
        }
    }

    public String login(String ip) {
        //判断一定不存在,直接返回，可能存在，到redis中确认
        boolean contain = bloomFilter.mightContain(ip);
        if (!contain) {
            return "布隆过滤器判断不属于白名单用户,登陆成功，ip: " + ip;
        }
        String key = CACHE_WHITE_LIST_KEY;
        Boolean member = redisTemplate.opsForSet().isMember(key, ip);
        if (member) {
            return "缓存中存在该用户，登陆成功， ip:" + ip;
        }
        return "不属于白名单用户，ip:" + ip;
    }
}
