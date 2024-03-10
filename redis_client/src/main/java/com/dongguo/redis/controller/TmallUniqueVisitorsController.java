package com.dongguo.redis.controller;

import com.dongguo.redis.service.TmallUniqueVisitorsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tmallUV")
@Tag(
        name = "TmallUniqueVisitorsController",
        description = "天猫UV控制器接口")
public class TmallUniqueVisitorsController {

    @Resource
    private TmallUniqueVisitorsService tmallUniqueVisitorsService;
    @Operation(
            summary = "addUV",
            description = "新增UV"
    )
    @PostMapping("/addUV")
    public void addUV() {
        tmallUniqueVisitorsService.addUV();
    }

    @Operation(
            summary = "getUV",
            description = "查询UV"
    )
    @GetMapping("/getUV")
    public Long getUV() {
        return tmallUniqueVisitorsService.getUV();
    }
}
