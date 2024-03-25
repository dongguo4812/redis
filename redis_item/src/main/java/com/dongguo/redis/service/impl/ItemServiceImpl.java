package com.dongguo.redis.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongguo.redis.entity.Item;
import com.dongguo.redis.entity.ItemStock;
import com.dongguo.redis.entity.PageDTO;
import com.dongguo.redis.mapper.ItemMapper;
import com.dongguo.redis.service.IItemService;
import com.dongguo.redis.service.IItemStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {
    @Autowired
    private IItemStockService stockService;

    @Override
    public PageDTO queryItemPage(Integer page, Integer size) {
        // 分页查询商品
        Page<Item> result = lambdaQuery()
                .ne(Item::getStatus, 3)
                .page(new Page<>(page, size));

        // 查询库存
        List<Item> list = result.getRecords().stream().peek(item -> {
            ItemStock stock = stockService.getById(item.getId());
            item.setStock(stock.getStock());
            item.setSold(stock.getSold());
        }).collect(Collectors.toList());

        // 封装返回
        return new PageDTO(result.getTotal(), list);
    }

    @Override
    @Transactional
    public void saveItem(Item item) {
        // 新增商品
        save(item);
        // 新增库存
        ItemStock stock = new ItemStock();
        stock.setId(item.getId());
        stock.setStock(item.getStock());
        stockService.save(stock);
    }

    @Override
    public void deleteById(Long id) {
        lambdaUpdate()
                .set(Item::getStatus, 3)
                .eq(Item::getId, id)
                .update();
    }
}
