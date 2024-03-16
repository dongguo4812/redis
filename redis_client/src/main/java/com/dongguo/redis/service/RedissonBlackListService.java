package com.dongguo.redis.service;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedissonBlackListService {
    @Resource
    private RBloomFilter<String> rBloomFilter;
    @Resource
    @Qualifier("redissonClientFactory")
    private  RedissonClient redissonClient;
    public String videoRecommendation(String videoId) {
        boolean contains = rBloomFilter.contains(videoId);
        //可能存在，查询redis确认
        if (contains) {
            RBucket<String> bucket = redissonClient.getBucket(videoId, new StringCodec());
            String result = bucket.get();
            //未推荐过，进行推荐
            if (StrUtil.isBlank(result)) {
                redissonClient.getBucket(videoId, new StringCodec()).set("推送视频" + videoId);
                return "视频推荐成功，videoId:" + videoId;
            }
            //已经推荐过
            return "视频已经推荐过，无需重复推荐，videoId:" + videoId;
        } else {
            //不存在，添加到布隆过滤器
            rBloomFilter.add(videoId);
            redissonClient.getBucket(videoId, new StringCodec()).set("推送视频" + videoId);
            return "视频推荐成功，videoId:" + videoId;
        }
    }
}
