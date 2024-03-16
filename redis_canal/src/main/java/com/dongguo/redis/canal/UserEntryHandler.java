package com.dongguo.redis.canal;

import com.alibaba.fastjson.JSON;
import com.dongguo.redis.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_USER_KEY;

/**
 * 获取到User对象后同步到缓存
 *
 * @author yang peng
 * @date 2019/4/1915:19
 */
@CanalTable(value = "t_user") //canal监控对应的数据库表
@Component
@Slf4j
public class UserEntryHandler implements EntryHandler<User> {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void insert(User user) {
        log.info("增加 {}", user);
        stringRedisTemplate.opsForValue().set(CACHE_USER_KEY + user.getId(), JSON.toJSONString(user));
    }

    @Override
    public void update(User before, User after) {
        log.info("修改 before {}", before);
        log.info("修改 after {}", after);
        stringRedisTemplate.opsForValue().set(CACHE_USER_KEY + before.getId(), JSON.toJSONString(before));
    }

    @Override
    public void delete(User user) {
        log.info("删除 {}", user);
        stringRedisTemplate.delete(CACHE_USER_KEY + user.getId());
    }
}