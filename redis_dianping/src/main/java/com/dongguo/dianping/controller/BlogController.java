package com.dongguo.dianping.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.dongguo.dianping.entity.DTO.UserDTO;
import com.dongguo.dianping.entity.POJO.Blog;
import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.service.IBlogService;
import com.dongguo.dianping.support.threadlocal.UserThreadLocalCache;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.dongguo.dianping.utils.SystemConstants.MAX_PAGE_SIZE;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 */
@RestController
@RequestMapping("/blog")
@Tag(
        name = "BlogController",
        description = "博客控制器接口")
public class BlogController {

    @Resource
    private IBlogService blogService;

    @PostMapping
    public Result saveBlog(@RequestBody Blog blog) {
        return blogService.saveBlog(blog);
    }
    @GetMapping("/{id}")
    public Result queryBlogById(@PathVariable("id") Long id) {
        return blogService.queryBlogById(id);
    }

//
//    @PutMapping("/like/{id}")
//    public Result likeBlog(@PathVariable("id") Long id) {
//        // 修改点赞数量
//        return blogService.likeBlog(id);
//    }
//
//    @GetMapping("/likes/{id}")
//    public Result queryBlogLikes(@PathVariable("id") Long id) {
//
//        return blogService.queryBlogLikes(id);
//    }
//
//    @GetMapping("/of/me")
//    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
//        // 获取登录用户
//        UserDTO user = UserThreadLocalCache.getUser();
//        // 根据用户查询
//        Page<Blog> page = blogService.query()
//                .eq("user_id", user.getId()).page(new Page<>(current, MAX_PAGE_SIZE));
//        // 获取当前页数据
//        List<Blog> records = page.getRecords();
//        return Result.ok(records);
//    }
//
//    @GetMapping("/hot")
//    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
//     return blogService.queryHotBlog(current);
//    }
//
//    /**
//     * 根据id查询博主的探店笔记
//     * @param current
//     * @param id
//     * @return
//     */
//    @GetMapping("/of/user")
//    public Result queryBlogByUserId(
//            @RequestParam(value = "current", defaultValue = "1") Integer current,
//            @RequestParam("id") Long id) {
//        // 根据用户查询
////        Page<Blog> page = blogService.query()
////                .eq("user_id", id).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
////        // 获取当前页数据
////        List<Blog> records = page.getRecords();
//        List<Blog> records = blogService.lambdaQuery().select().eq(Blog::getUserId, id).list();
//        return Result.ok(records);
//    }
//    @GetMapping("/of/follow")
//    public Result queryBlogOfFollow(@RequestParam("lastId") Long max,@RequestParam(value = "offset",defaultValue = "0") Integer offset ){
//        return blogService.queryBlogOfFollow(max,offset);
//
//    }
}
