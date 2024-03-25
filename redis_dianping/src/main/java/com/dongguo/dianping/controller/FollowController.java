package com.dongguo.dianping.controller;

import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.service.IFollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/follow")
@Tag(
        name = "FollowController",
        description = "关注控制器接口")
public class FollowController {

    @Autowired
    private IFollowService followService;

    //关注
    @PutMapping("/{id}/{isFollow}")
    @Operation(
            summary = "follow",
            description = "关注"
    )
    public Result follow(@PathVariable("id") Long followUserId, @PathVariable("isFollow") Boolean isFollow) {
        return followService.follow(followUserId, isFollow);
    }

    //取消关注
    @GetMapping("/or/not/{id}")
    @Operation(
            summary = "isFollow",
            description = "取消关注"
    )
    public Result isFollow(@PathVariable("id") Long followUserId) {
        return followService.isFollow(followUserId);
    }

    @GetMapping("/common/{id}")
    @Operation(
            summary = "followCommons",
            description = "共同关系"
    )
    public Result followCommons(@PathVariable("id") Long id) {
        return followService.followCommons(id);
    }

}
