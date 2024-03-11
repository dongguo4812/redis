package com.dongguo.redis.service;

import com.google.common.hash.BloomFilter;
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

    public void initWhiteList() {
        String key = CACHE_WHITE_LIST_KEY;
        //布隆过滤器预计插入的元素数量为100W
        int COUNT = 10000 * 100;
        //误判率  大于0小于1.0。值越小误判的个数也就越少
        double FPP = 0.01;
        Random random = new Random();
        //模拟200个ip的白名单
        for (int i = 0; i < 200; i++) {
            String randomIp = random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
            bloomFilter.put(randomIp);
            redisTemplate.opsForSet().add(key, randomIp);
        }
    }

    public String login(String ip) {
        boolean contain = bloomFilter.mightContain(ip);
        if (contain) {
            return "布隆过滤器判断属于白名单用户,登陆成功，ip: " + ip;
        }
        String key = CACHE_WHITE_LIST_KEY;
        Boolean member = redisTemplate.opsForSet().isMember(key, ip);
        if (member) {
            return "缓存中存在该用户，登陆成功， ip:" + ip;
        }
        return null;
    }
}
