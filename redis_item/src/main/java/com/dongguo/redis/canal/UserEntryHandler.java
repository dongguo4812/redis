package com.dongguo.redis.canal;

import com.dongguo.redis.entity.Item;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.xiaowu.behappy.canal.client.annotation.CanalTable;
import org.xiaowu.behappy.canal.client.handler.EntryHandler;


/**
 * 获取到Item对象后同步到缓存
 *
 */
@Component
@Slf4j
@CanalTable(value = "tb_item")
public class UserEntryHandler implements EntryHandler<Item> {
    private static final String CACHE_ITEM_KEY = "item:id:";
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void insert(Item item) {
        log.info("增加 {}", item);
        redisTemplate.opsForValue().set(CACHE_ITEM_KEY + item.getId(), item);
    }

    @Override
    public void update(Item before, Item after) {
        log.info("修改 before {}", before);
        log.info("修改 after {}", after);
        redisTemplate.opsForValue().set(CACHE_ITEM_KEY + before.getId(), before);
    }

    @Override
    public void delete(Item item) {
        log.info("删除 {}", item);
        redisTemplate.delete(CACHE_ITEM_KEY + item.getId());
    }
}