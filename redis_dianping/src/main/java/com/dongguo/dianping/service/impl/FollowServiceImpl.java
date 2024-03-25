package com.dongguo.dianping.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongguo.dianping.entity.DTO.UserDTO;
import com.dongguo.dianping.entity.POJO.Follow;
import com.dongguo.dianping.entity.POJO.User;
import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.mapper.FollowMapper;
import com.dongguo.dianping.service.IFollowService;
import com.dongguo.dianping.service.IUserService;
import com.dongguo.dianping.support.threadlocal.UserThreadLocalCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dongguo.dianping.utils.RedisConstants.USER_FOLLOWS;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IUserService userService;
    @Override
    public Result follow(Long followUserId, Boolean isFollow) {
        Long userId = UserThreadLocalCache.getUser().getId();
        String key = USER_FOLLOWS + userId;
        if (BooleanUtil.isTrue(isFollow)) {
            //关注
            Follow follow = new Follow()
                    .setUserId(userId)
                    .setFollowUserId(followUserId);
            boolean isSuccess = save(follow);
            if (isSuccess) {
                // 把关注用户的id，放入redis的set集合 sadd userId followerUserId
                stringRedisTemplate.opsForSet().add(key, followUserId.toString());
            }
        } else {
            //取关
            LambdaUpdateWrapper<Follow> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Follow::getUserId, userId)
                    .eq(Follow::getFollowUserId, followUserId);
            boolean isSuccess = remove(wrapper);
            if (isSuccess) {
                // 把关注用户的id从Redis集合中移除
                stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
            }
        }
        return Result.ok();
    }

    @Override
    public Result isFollow(Long followUserId) {
        Long userId = UserThreadLocalCache.getUser().getId();
        Long count = lambdaQuery().select()
                .eq(Follow::getUserId, userId)
                .eq(Follow::getFollowUserId, followUserId)
                .count();
        return Result.ok(count > 0);
    }

    @Override
    public Result followCommons(Long id) {
        Long userId = UserThreadLocalCache.getUser().getId();

        String userKey = USER_FOLLOWS + userId;
        String otherUserKey = USER_FOLLOWS + id;
        Set<String> followCommonIds = stringRedisTemplate.opsForSet().intersect(userKey, otherUserKey);
        if (CollUtil.isEmpty(followCommonIds)){
            // 无交集
            return Result.ok(Collections.emptyList());
        }

        // 3.解析id集合
        List<Long> ids = followCommonIds.stream().map(Long::valueOf).collect(Collectors.toList());
        List<User> followCommonUsers = userService.listByIds(ids);
        List<UserDTO> users = followCommonUsers.stream().map(user ->
           BeanUtil.copyProperties(user, UserDTO.class)
        ).collect(Collectors.toList());

        return Result.ok(users);
    }
}
