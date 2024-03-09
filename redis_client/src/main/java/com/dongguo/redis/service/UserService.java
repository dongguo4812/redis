package com.dongguo.redis.service;

import com.dongguo.redis.entity.User;
import com.dongguo.redis.mapper.UserMapper;
import com.dongguo.redis.utils.CacheKeyUtil;
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
            String Key = CacheKeyUtil.CACHE_KEY_USER + user.getId();
            redisTemplate.opsForValue().set(Key, user);
        }
    }

    public void deleteUser(Long id) {
        int count = userMapper.deleteByPrimaryKey(id);
        if (count > 0) {
            String Key = CacheKeyUtil.CACHE_KEY_USER + id;
            redisTemplate.delete(Key);
        }
    }

    public void editUser(User user) {
        User oldUser = userMapper.selectByPrimaryKey(user);
        if (null == oldUser) {
            return;
        }
        int count = userMapper.updateByPrimaryKey(user);
        if (count > 0) {
            String Key = CacheKeyUtil.CACHE_KEY_USER + user.getId();
            redisTemplate.delete(Key);
        }

    }

    public User findUser(Long id) {
        String Key = CacheKeyUtil.CACHE_KEY_USER + id;
        User user = (User) redisTemplate.opsForValue().get(Key);
        if (null == user) {
            user = userMapper.selectByPrimaryKey(id);
            if (null == user) {
                return null;
            }
            redisTemplate.opsForValue().set(Key, user, 1000, TimeUnit.MINUTES);
        }
        return user;
    }
}
