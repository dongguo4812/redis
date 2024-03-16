package com.dongguo.redis.canal;

import com.dongguo.redis.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.xiaowu.behappy.canal.client.annotation.CanalTable;
import org.xiaowu.behappy.canal.client.handler.EntryHandler;


/**
 * 获取到User对象后同步到缓存
 *
 * @author yang peng
 * @date 2019/4/1915:19
 */
@Component
@Slf4j
@CanalTable(value = "t_user")
public class UserEntryHandler implements EntryHandler<User> {
    private static final String CACHE_USER_KEY = "redis:user:";
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void insert(User user) {
        log.info("增加 {}", user);
        redisTemplate.opsForValue().set(CACHE_USER_KEY + user.getId(), user);
    }

    @Override
    public void update(User before, User after) {
        log.info("修改 before {}", before);
        log.info("修改 after {}", after);
        redisTemplate.opsForValue().set(CACHE_USER_KEY + before.getId(), before);
    }

    @Override
    public void delete(User user) {
        log.info("删除 {}", user);
        redisTemplate.delete(CACHE_USER_KEY + user.getId());
    }
}