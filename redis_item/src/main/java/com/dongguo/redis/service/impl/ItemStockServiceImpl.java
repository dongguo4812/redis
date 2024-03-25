package com.dongguo.redis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongguo.redis.entity.ItemStock;
import com.dongguo.redis.mapper.ItemStockMapper;
import com.dongguo.redis.service.IItemStockService;
import org.springframework.stereotype.Service;

@Service
public class ItemStockServiceImpl extends ServiceImpl<ItemStockMapper, ItemStock> implements IItemStockService {
}
