package com.dongguo.redis.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dongguo.redis.util.CacheConstants.CACHE_KEY_GEO;

/**
 * @Author: Administrator
 * @Date: 2024-02-26
 */
@Service
@Slf4j
public class GeoService {
    @Resource
    private RedisTemplate redisTemplate;


    public String addGeo() {
        String key = CACHE_KEY_GEO;
        Map<String, Point> map = new HashMap<>();
        map.put("天安门", new Point(116.403963, 39.915119));
        map.put("故宫", new Point(116.403414, 39.924091));
        map.put("长城", new Point(116.746579, 40.482509));
        redisTemplate.opsForGeo().add(key, map);

        return map.toString();
    }

    public List getGeo(String member) {
        String key = CACHE_KEY_GEO;
        return redisTemplate.opsForGeo().position(key, member);
    }

    public List getGeoHash(String member) {
        String key = CACHE_KEY_GEO;
        return redisTemplate.opsForGeo().hash(key, member);
    }

    public GeoResults getGeoRadius(Point point) {
        String key = CACHE_KEY_GEO;
        //当前坐标位置
        Circle circle = new Circle(point, Metrics.MILES.getMultiplier());
        //返回50条
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending();
        return redisTemplate.opsForGeo().radius(key, circle, args);

    }

    public GeoResults getGeoRadiusByMember(String member) {
        String key = CACHE_KEY_GEO;
        //返回50条
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending();
        //查找半径10公里
        Distance distance = new Distance(10, Metrics.KILOMETERS);
        return redisTemplate.opsForGeo().radius(key, member, distance, args);
    }
}
