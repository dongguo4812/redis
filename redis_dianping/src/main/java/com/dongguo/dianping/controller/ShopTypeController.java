package com.dongguo.dianping.controller;


import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.service.IShopTypeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    @Resource
    private IShopTypeService typeService;

    @GetMapping("list")
    @Operation(
            summary = "list",
            description = "列表查询"
    )
    public Result queryTypeList() {
        return typeService.queryTypeList();
    }
}
