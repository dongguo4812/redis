package com.dongguo.redis.controller;

import com.dongguo.redis.entity.Product;
import com.dongguo.redis.service.JhsProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Administrator
 * @Date: 2024-02-27
 */
@RestController
@RequestMapping("/jhs")
@Tag(
        name = "JhsProductController",
        description = "聚划算控制器接口")
public class JhsProductController {

    @Resource
    private JhsProductService jhsProductService;


    /***
     * 分页查询聚划算商品
     */
    @Operation(
            summary = "分页查询聚划算商品",
            description = "分页查询聚划算商品，并返回响应结果信息"

    )
    @GetMapping("/find")
    public List<Product> findUser(int page, int size) {
        return jhsProductService.findJhsProducts(page, size);
    }
}
