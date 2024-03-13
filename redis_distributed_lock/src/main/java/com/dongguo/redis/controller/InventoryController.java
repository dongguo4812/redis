package com.dongguo.redis.controller;

import com.dongguo.redis.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Administrator
 * @Date: 2024-03-12
 */
@RestController
@RequestMapping("/inventory")
@Tag(
        name = "InventoryController",
        description = "库存控制器接口")
public class InventoryController {

    @Resource
    private InventoryService inventoryService;

    @Operation(
            summary = "initInventory",
            description = "初始化库存"
    )
    @PostMapping("/initInventory")
    public void initInventory() {
        inventoryService.initInventory();
    }

    @Operation(
            summary = "saleTicket",
            description = "售票"
    )
    @GetMapping("/saleTicket")
    public String saleTicket() {
        return inventoryService.saleTicketV7();
    }
}
