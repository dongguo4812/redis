package com.dongguo.redis.controller;

import com.dongguo.redis.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Operation(
            summary = "新增订单",
            description = "新增订单"
    )
    @PostMapping("/add")
    public void addOrder(){
        orderService.addOrder();
    }
    @Operation(
            summary = "查询订单",
            description = "查询订单"
    )
    @GetMapping("/get/{orderId}")
    public String getOrder(@PathVariable(value = "orderId") Long orderId){
       return orderService.getOrder(orderId);
    }
}
