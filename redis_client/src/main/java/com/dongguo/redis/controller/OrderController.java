package com.dongguo.redis.controller;

import com.dongguo.redis.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Tag(
        name = "OrderController",
        description = "订单控制器接口")
public class OrderController {
    @Resource
    private OrderService orderService;

    @Operation(
            summary = "新增订单",
            description = "新增订单"
    )
    @PostMapping("/add")
    public void addOrder() {
        orderService.addOrder();
    }

    @Operation(
            summary = "查询订单",
            description = "查询订单"
    )
    @GetMapping("/get/{orderId}")
    public String getOrder(@PathVariable(value = "orderId") Long orderId) {
        return orderService.getOrder(orderId);
    }
}
