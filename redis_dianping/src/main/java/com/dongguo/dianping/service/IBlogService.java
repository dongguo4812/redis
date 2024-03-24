package com.dongguo.dianping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongguo.dianping.entity.POJO.Blog;
import com.dongguo.dianping.entity.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IBlogService extends IService<Blog> {
    Result saveBlog(Blog blog);

    Result queryBlogById(Long id);
//
//    Result likeBlog(Long id);
//
//    Result queryHotBlog(Integer current);
//
//    Result queryBlogLikes(Long id);
//
//
//
//    Result queryBlogOfFollow(Long max, Integer offset);
}
