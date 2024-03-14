package com.dongguo.redis.myredis;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.locks.Lock;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_INVENTORY_LOCK_KEY;
import static com.dongguo.redis.utils.CommonConst.REDIS;

/**
 * 创建redis分布式锁的工厂类
 */
@Component
public class DistributedLockFactory {
    @Autowired
    private RedisTemplate redisTemplate;
    private String uuid;

    public DistributedLockFactory() {
        //每个实例对应一个全局唯一uuid
        this.uuid = IdUtil.fastUUID();
    }

    public Lock getDistributedLock(String lockType) {
        if (StrUtil.isBlank(lockType)) {
            return null;
        }
        if (REDIS.equalsIgnoreCase(lockType)) {
            return new RedisDistributedLock(redisTemplate, CACHE_INVENTORY_LOCK_KEY, uuid);
        }
        return null;
    }
}
