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
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_HOTEL_KEY;

@Slf4j
@Service
public class HotelService {
    @Resource
    private RedisTemplate redisTemplate;

    public String addHotel() {
        String key = CACHE_HOTEL_KEY;
        //初始化三个酒店
        Map<String, Point> map = new HashMap<>();
        map.put("我的位置", new Point(116.403669, 39.915312));
        map.put("锦江酒店", new Point(116.403963, 39.915119));
        map.put("华住酒店", new Point(116.403414, 39.924091));
        map.put("开元酒店", new Point(116.746579, 40.482509));
        redisTemplate.opsForGeo().add(key, map);
        return map.toString();
    }

    public List getGeo(String member) {
        String key = CACHE_HOTEL_KEY;
        return redisTemplate.opsForGeo().position(key, member);
    }

    public GeoResults getGeoRadius(Point point) {
        String key = CACHE_HOTEL_KEY;
        //查找半径10公里
        Distance distance = new Distance(10, Metrics.KILOMETERS);
        Circle circle = new Circle(point, distance);
        //返回50条
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands
                .GeoRadiusCommandArgs.newGeoRadiusArgs()    //创建地理位置查询的参数构造器。
                .includeDistance()      //返回的地理位置都会附带其到圆心的距离。
                .includeCoordinates()   //返回的地理位置都会附带其具体的经纬度坐标。
                .sortAscending();   //按照距离圆心的距离进行升序排序。
        return redisTemplate.opsForGeo().radius(key, circle, args);
    }

    public GeoResults getGeoRadiusByMember(String member) {
        String key = CACHE_HOTEL_KEY;
        //返回50条
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands
                .GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending();
        //查找半径10公里
        Distance distance = new Distance(10, Metrics.KILOMETERS);
        return redisTemplate.opsForGeo().radius(key, member, distance, args);
    }
}
