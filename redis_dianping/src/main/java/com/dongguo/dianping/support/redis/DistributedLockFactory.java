package com.dongguo.dianping.support.redis;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.locks.Lock;

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

    public Lock getDistributedLock(String lockType, String key) {
        if (StrUtil.isBlank(lockType)) {
            return null;
        }
        if ("redis".equalsIgnoreCase(lockType)) {
            return new RedisDistributedLock(redisTemplate, key, uuid);
        }
        return null;
    }
}
