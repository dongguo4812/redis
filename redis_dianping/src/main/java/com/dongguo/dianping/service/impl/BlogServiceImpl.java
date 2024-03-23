package com.dongguo.dianping.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongguo.dianping.entity.DTO.UserDTO;
import com.dongguo.dianping.entity.POJO.Blog;
import com.dongguo.dianping.entity.POJO.User;
import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.mapper.BlogMapper;
import com.dongguo.dianping.service.IBlogService;
import com.dongguo.dianping.service.IUserService;
import com.dongguo.dianping.support.threadlocal.UserThreadLocalCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
    @Autowired
    private IUserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public Result saveBlog(Blog blog) {
        // 获取登录用户
        UserDTO user = UserThreadLocalCache.getUser();
        blog.setUserId(user.getId());

        String title = blog.getTitle();
        if (StrUtil.isBlank(title)){
            return Result.fail("标题不能为空");
        }
        // 保存探店博文
        boolean isSuccess = save(blog);
        if (!isSuccess){
            return Result.fail("新增笔记失败");
        }
//        List<Follow> followList = followService.lambdaQuery().eq(Follow::getFollowUserId, user.getId()).list();
//
//        followList.forEach(follow -> {
//            Long userId = follow.getUserId();
//            String key = RedisConstants.FEED_KEY + userId;
//            //将笔记id推送给粉丝
//            stringRedisTemplate.opsForZSet().add(key, blog.getId().toString(), System.currentTimeMillis());
//        });

        // 返回id
        return Result.ok();
    }
//    @Override
//    public Result queryBlogById(Long id) {
//        Blog blog = getById(id);
//        if (ObjectUtil.isEmpty(blog)){
//            return Result.fail("笔记不存在");
//        }
//        queryBlogUser(blog);
//        isBlogLiked(blog);
//        return Result.ok(blog);
//
//    }
//
//    private void isBlogLiked(Blog blog) {
//        Long userId = UserThreadLocalCache.getUser().getId();
//
//        Double score = stringRedisTemplate.opsForZSet().score(RedisConstants.BLOG_LIKED_KEY + blog.getId(), userId.toString());
//        blog.setIsLike(ObjectUtil.isNotEmpty(score));
//    }
//
//    @Override
//    public Result likeBlog(Long id) {
//        Long userId = UserThreadLocalCache.getUser().getId();
//
//        Double score = stringRedisTemplate.opsForZSet().score(RedisConstants.BLOG_LIKED_KEY + id, userId.toString());
//        if (ObjectUtil.isEmpty(score)){
//            //为点过赞，新增点赞并缓存
//            boolean result = update().setSql("liked = liked + 1").eq("id", id).update();
//            if (!result){
//                return Result.fail("点赞失败");
//            }
////            stringRedisTemplate.opsForSet().add(RedisConstants.BLOG_LIKED_KEY + id , userId.toString());
//            stringRedisTemplate.opsForZSet().add(RedisConstants.BLOG_LIKED_KEY + id , userId.toString(),System.currentTimeMillis());
//        }else {
//            //点过赞  取消点赞并删除缓存
//            boolean result = update().setSql("liked = liked - 1").eq("id", id).update();
//            if (!result){
//                return Result.fail("点赞失败");
//            }
////            stringRedisTemplate.opsForSet().remove(RedisConstants.BLOG_LIKED_KEY + id , userId.toString());
//            stringRedisTemplate.opsForZSet().remove(RedisConstants.BLOG_LIKED_KEY + id , userId.toString());
//        }
//        return Result.ok();
//    }
//
//    @Override
//    public Result queryHotBlog(Integer current) {
//        // 根据用户查询
//        Page<Blog> page = query()
//                .orderByDesc("liked")
//                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
//        // 获取当前页数据
//        List<Blog> records = page.getRecords();
//        // 查询用户
//        records.forEach(blog ->{
//            queryBlogUser(blog);
//            isBlogLiked(blog);
//        });
//        return Result.ok(records);
//    }
//
//    @Override
//    public Result queryBlogLikes(Long id) {
//
//        Set<String> topFive = stringRedisTemplate.opsForZSet().range(RedisConstants.BLOG_LIKED_KEY + id, 0, 4);
//        if (CollUtil.isEmpty(topFive)){
//            return Result.ok(Collections.emptyList());
//        }
//
//        List<Long> ids = topFive.stream().map(Long::valueOf).collect(Collectors.toList());
//        //userService.listByIds(ids)//数据库in(5,1)查询出来的数据并不是和传入id的顺序一致
//        String idsStr = StrUtil.join(",", ids);
//        userService.lambdaQuery().select().in(User::getId,ids).last("ORDER BY FIELD(id," + idsStr + ")").list();
//        List<UserDTO> userDTOList = userService.listByIds(ids)
//                .stream().map((user) -> BeanUtil.copyProperties(user, UserDTO.class)).collect(Collectors.toList());
//        return Result.ok(userDTOList);
//    }
//
//
//
//    @Override
//    public Result queryBlogOfFollow(Long max, Integer offset) {
//        Long userId = UserThreadLocalCache.getUser().getId();
//        String key = RedisConstants.FEED_KEY + userId;
//        //ZREVRANGEBYSCORE key Max Min LIMIT offset count
//        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, max, offset, 2);
//        // 3.非空判断
//        if (typedTuples == null || typedTuples.isEmpty()) {
//            return Result.ok();
//        }
//        List<Long> ids = new ArrayList<>(typedTuples.size());
//        long minTime = 0;
//        int offsetCount = 1;
//        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
//            String idStr = tuple.getValue();
//            ids.add(Long.valueOf(idStr));
//            long time = tuple.getScore().longValue();
//            if (time == minTime){
//                offsetCount++;
//            }else {
//                minTime = time;
//                offsetCount = 1;
//            }
//        }
//        String idsStr = StrUtil.join(",", ids);
//        List<Blog> blogs = query().in("id", ids).last("ORDER BY FIELD(id," + idsStr + ")").list();
//       blogs.forEach(blog->{
//           // 5.1.查询blog有关的用户
//           queryBlogUser(blog);
//           // 5.2.查询blog是否被点赞
//           isBlogLiked(blog);
//       });
//        // 6.封装并返回
//        ScrollResult result = new ScrollResult();
//        result.setList(blogs);
//        result.setOffset(offsetCount);
//        result.setMinTime(minTime);
//
//        return Result.ok(result);
//    }
//
//    private void queryBlogUser(Blog blog) {
//        Long userId = blog.getUserId();
//        User user = userService.getById(userId);
//        blog.setIcon(user.getIcon());
//        blog.setName(user.getNickName());
//    }
}
