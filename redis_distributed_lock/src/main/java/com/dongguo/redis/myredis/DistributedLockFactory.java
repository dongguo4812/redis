package com.dongguo.redis.myredis;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.locks.Lock;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_INVENTORY_LOCK_KEY;
import static com.dongguo.redis.utils.CacheKeyUtil.ZOOKEEPER_INVENTORY_LOCK_KEY;

/**
 * 创建redis分布式锁的工厂类
 */
@Component
public class DistributedLockFactory {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private String lockName;
    private String uuid;

    public DistributedLockFactory() {
        //每个实例对应一个全局唯一uuid
        this.uuid = IdUtil.fastUUID();
    }

    public Lock getDistributedLock(String lockType) {
        if (StrUtil.isBlank(lockType)) {
            return null;
        }
        if ("redis".equalsIgnoreCase(lockType)) {
            this.lockName = CACHE_INVENTORY_LOCK_KEY;
            return new RedisDistributedLock(redisTemplate, lockName, uuid);
        } else if ("zookeeper".equalsIgnoreCase(lockType)) {
            this.lockName = ZOOKEEPER_INVENTORY_LOCK_KEY;
            //创建zookeeper的分布式锁
            return null;
        }
        return null;
    }
}
