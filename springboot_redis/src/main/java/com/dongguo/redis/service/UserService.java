package com.dongguo.redis.service;

import cn.hutool.core.util.ObjectUtil;
import com.dongguo.redis.entity.User;
import com.dongguo.redis.mapper.UserMapper;
import com.dongguo.redis.util.CacheConstants;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {


    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate redisTemplate;

    public void addUser(User user) {
        int count = userMapper.insertSelective(user);
        if (count > 0) {
            String Key = CacheConstants.CACHE_KEY_USER + user.getId();
            redisTemplate.opsForValue().set(Key, user);
        }
    }

    public void deleteUser(Long id) {
        int count = userMapper.deleteByPrimaryKey(id);
        if (count > 0) {
            String Key = CacheConstants.CACHE_KEY_USER + id;
            redisTemplate.delete(Key);
        }
    }

    public void editUser(User user) {
        User oldUser = userMapper.selectByPrimaryKey(user);
        if (ObjectUtil.isEmpty(oldUser)) {
            return;
        }
        int count = userMapper.updateByPrimaryKey(user);
        if (count > 0) {
            String Key = CacheConstants.CACHE_KEY_USER + user.getId();
            redisTemplate.delete(Key);
        }

    }

    /**
     * 分布式
     * @param id
     * @return
     */
    public User findUser(Long id) {
        String Key = CacheConstants.CACHE_KEY_USER + id;
        User user = (User) redisTemplate.opsForValue().get(Key);
        if (ObjectUtil.isEmpty(user)) {
            String lockKey = CacheConstants.CACHE_KEY_USER_LOCK + id;
            Boolean tryLock = redisTemplate.opsForValue().setIfAbsent(lockKey, id, 100, TimeUnit.MINUTES);
            if (tryLock) {
                try {
                    user = userMapper.selectByPrimaryKey(id);
                    if (ObjectUtil.isEmpty(user)) {
                        return null;
                    }
                    redisTemplate.opsForValue().set(Key, user, 1000, TimeUnit.MINUTES);
                } finally {
                    redisTemplate.delete(lockKey);
                }
            }
        }
        return user;
    }

    /**
     * 多线程
     * @param id
     * @return
     */
    public User findUserV2(Long id) {
        String Key = CacheConstants.CACHE_KEY_USER + id;
        User user = (User) redisTemplate.opsForValue().get(Key);
        if (ObjectUtil.isEmpty(user)) {
            synchronized (UserService.class){
                 user = (User) redisTemplate.opsForValue().get(Key);
                if (ObjectUtil.isEmpty(user)) {
                    user = userMapper.selectByPrimaryKey(id);
                    if (ObjectUtil.isEmpty(user)) {
                        return null;
                    }
                    redisTemplate.opsForValue().set(Key, user, 1000, TimeUnit.MINUTES);
                }
            }
        }
        return user;
    }
}
