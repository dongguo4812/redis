package com.dongguo.redis.controller;

import com.dongguo.redis.entity.Item;
import com.dongguo.redis.entity.ItemStock;
import com.dongguo.redis.entity.PageDTO;
import com.dongguo.redis.service.IItemService;
import com.dongguo.redis.service.IItemStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("item")
@Tag(
        name = "ItemController",
        description = "商品控制器接口")
public class ItemController {

    @Autowired
    private IItemService itemService;
    @Autowired
    private IItemStockService stockService;

//    @Autowired
//    private Cache<Long, Item> itemCache;
//    @Autowired
//    private Cache<Long, ItemStock> stockCache;

    @GetMapping("list")
    @Operation(
            summary = "queryItemPage",
            description = "分页列表"
    )
    public PageDTO queryItemPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size) {
        return itemService.queryItemPage(page, size);
    }

    @PostMapping
    @Operation(
            summary = "saveItem",
            description = "新增商品"
    )
    public void saveItem(@RequestBody Item item) {
        itemService.saveItem(item);
    }

    @PutMapping
    @Operation(
            summary = "updateItem",
            description = "更新商品"
    )
    public void updateItem(@RequestBody Item item) {
        itemService.updateById(item);
    }

    @PutMapping("stock")
    @Operation(
            summary = "updateStock",
            description = "更新库存"
    )
    public void updateStock(@RequestBody ItemStock itemStock) {
        stockService.updateById(itemStock);
    }

    @DeleteMapping("/{id}")
    @PutMapping("stock")
    @Operation(
            summary = "deleteById",
            description = "删除商品"
    )
    public void deleteById(@PathVariable("id") Long id) {
        itemService.deleteById(id);
    }

//    @GetMapping("/{id}")
//    public Item findById(@PathVariable("id") Long id) {
//        return itemCache.get(id, key -> itemService.query()
//                .ne("status", 3).eq("id", key)
//                .one()
//        );
//    }
//
//    @GetMapping("/stock/{id}")
//    public ItemStock findStockById(@PathVariable("id") Long id) {
//        return stockCache.get(id, key -> stockService.getById(key));
//    }
}
