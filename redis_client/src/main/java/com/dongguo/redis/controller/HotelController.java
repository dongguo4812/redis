package com.dongguo.redis.controller;

import com.dongguo.redis.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel")
@Tag(
        name = "HotelController",
        description = "美团酒店控制器接口")
public class HotelController {
    @Resource
    private HotelService hotelService;

    @Operation(
            summary = "addHotel",
            description = "新增酒店"
    )
    @PostMapping("/addHotel")
    public String addHotel() {
        return hotelService.addHotel();
    }

    @Operation(
            summary = "getGeo",
            description = "根据名称获取经纬度"
    )
    @GetMapping("/getGeo/{member}")
    public List getGeo(@PathVariable(value = "member") String member) {
        return hotelService.getGeo(member);
    }

    /**
     *   {
     *     "x": 116.40366822481155,
     *     "y": 39.91531234219449
     *   }
     * @param point
     * @return
     */
    @Operation(
            summary = "getGeoRadius",
            description = "通过经纬度查找附近地点"
    )
    @PostMapping("/getGeoRadius")
    public GeoResults getGeoRadius(@RequestBody Point point) {
        return hotelService.getGeoRadius(point);
    }

    @Operation(
            summary = "getGeoRadiusByMember",
            description = "通过地址查找附近地点"
    )
    @GetMapping("/getGeoRadiusByMember/{member}")
    public GeoResults getGeoRadiusByMember(@PathVariable(value = "member") String member) {
        return hotelService.getGeoRadiusByMember(member);
    }
}
