package com.dongguo.redis.service;

import com.dongguo.redis.entity.User;
import com.dongguo.redis.mapper.UserMapper;
import com.dongguo.redis.utils.CacheKeyUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_USER_BLOOMFILTER_KEY;

@Service
@Slf4j
public class UserService {


    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 初始化布隆过滤器
     */
    @PostConstruct
    public void initBloomFilter() {
        //符合条件的元素。
        List<User> userList = userMapper.selectAll();
        userList.forEach(user -> {
            String key = CACHE_USER_BLOOMFILTER_KEY + user.getId();
            //计算hash值，可能存在负值，取绝对值。
            int hashValue = Math.abs(key.hashCode());
            //通过hash值计算出对应的二级制数组的坑位:hashValue 除以2的32次方的余数
            long index = (long) (hashValue % Math.pow(2, 32));
            log.info(key + "对应的index：{}", index);
            //将对应坑位的值修改为1
            redisTemplate.opsForValue().setBit(key, index, Boolean.TRUE);
        });
    }

    public void addUser(User user) {
        int count = userMapper.insertSelective(user);
        if (count > 0) {
            String Key = CacheKeyUtil.CACHE_USER_KEY + user.getId();
            redisTemplate.opsForValue().set(Key, user);
        }
    }

    public void deleteUser(Long id) {
        int count = userMapper.deleteByPrimaryKey(id);
        if (count > 0) {
            String Key = CacheKeyUtil.CACHE_USER_KEY + id;
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
            String Key = CacheKeyUtil.CACHE_USER_KEY + user.getId();
            redisTemplate.delete(Key);
        }

    }

    public User findUser(Long id) {
        String Key = CacheKeyUtil.CACHE_USER_KEY + id;
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

    /**
     * 分布式锁
     *
     * @param id
     * @return
     */
    public User findUserV2(Long id) {
        String Key = CacheKeyUtil.CACHE_USER_KEY + id;
        User user = (User) redisTemplate.opsForValue().get(Key);
        if (null == user) {
            String lockKey = CacheKeyUtil.CACHE_USER_LOCK_KEY + id;
            Boolean tryLock = redisTemplate.opsForValue().setIfAbsent(lockKey, id, 100, TimeUnit.MINUTES);
            if (tryLock) {
                try {
                    user = userMapper.selectByPrimaryKey(id);
                    if (null == user) {
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
     * 单机锁 双重校验
     *
     * @param id
     * @return
     */
    public User findUserV3(Long id) {
        String bloomFilterKey = CacheKeyUtil.CACHE_USER_BLOOMFILTER_KEY + id;
        //计算hash值，可能存在负值，取绝对值。
        int hashValue = Math.abs(bloomFilterKey.hashCode());
        //通过hash值计算出对应的二级制数组的坑位:hashValue 除以2的32次方的余数
        long index = (long) (hashValue % Math.pow(2, 32));
        Boolean exists = redisTemplate.opsForValue().getBit(bloomFilterKey, index);
        if (!exists){
            log.info("布隆过滤器中不存在id为{}的用户",id);
            return null;
        }
        String Key = CacheKeyUtil.CACHE_USER_KEY + id;
        User user = (User) redisTemplate.opsForValue().get(Key);
        if (null == user) {
            synchronized (UserService.class) {
                user = (User) redisTemplate.opsForValue().get(Key);
                if (null == user) {
                    log.info("缓存中不存在id为{}的用户",id);
                    user = userMapper.selectByPrimaryKey(id);
                    if (null == user) {
                        log.info("数据库中不存在id为{}的用户",id);
                        return null;
                    }
                    redisTemplate.opsForValue().set(Key, user, 1000, TimeUnit.MINUTES);
                }
            }
        }
        return user;
    }
}
