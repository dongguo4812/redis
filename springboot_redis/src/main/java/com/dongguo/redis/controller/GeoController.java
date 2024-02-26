package com.dongguo.redis.controller;

import com.dongguo.redis.service.GeoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Administrator
 * @Date: 2024-02-26
 */
@RestController
@RequestMapping("/geo")
@Tag(
        name = "GeoController",
        description = "地图位置控制器接口")
public class GeoController {

    @Resource
    private GeoService geoService;
    @Operation(
            summary = "新增GEO",
            description = "新增GEO"
    )
    @PostMapping("/addGeo")
    public String addGeo() {
        return geoService.addGeo();
    }

    @Operation(
            summary = "获取GEO",
            description = "获取GEO"
    )
    @GetMapping("/getGeo")
    public List getGeo(String member) {
        return geoService.getGeo(member);
    }

    @Operation(
            summary = "获取GEOHash",
            description = "获取GEOHash"
    )
    @GetMapping("/getGeoHash")
    public List getGeoHash(String member) {
        return geoService.getGeoHash(member);
    }

    @Operation(
            summary = "通过经纬度查找附近地点",
            description = "通过经纬度查找附近地点"
    )
    @GetMapping("/getGeoRadius")
    public GeoResults getGeoRadius(@RequestBody Point point) {
        return geoService.getGeoRadius(point);
    }

    @Operation(
            summary = "通过地址查找附近地点",
            description = "通过地址查找附近地点"
    )
    @GetMapping("/getGeoRadiusByMember")
    public GeoResults getGeoRadiusByMember(@PathVariable String member) {
        return geoService.getGeoRadiusByMember(member);
    }
}
