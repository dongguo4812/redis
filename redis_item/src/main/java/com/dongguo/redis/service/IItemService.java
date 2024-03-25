package com.dongguo.redis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongguo.redis.entity.Item;
import com.dongguo.redis.entity.PageDTO;


public interface IItemService extends IService<Item> {
    void saveItem(Item item);

    void deleteById(Long id);

    PageDTO queryItemPage(Integer page, Integer size);
}
