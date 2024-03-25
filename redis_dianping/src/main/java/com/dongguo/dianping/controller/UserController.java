package com.dongguo.dianping.controller;

import cn.hutool.core.bean.BeanUtil;
import com.dongguo.dianping.entity.BO.LoginFormBO;
import com.dongguo.dianping.entity.DTO.UserDTO;
import com.dongguo.dianping.entity.POJO.User;
import com.dongguo.dianping.entity.POJO.UserInfo;
import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.service.IUserInfoService;
import com.dongguo.dianping.service.IUserService;
import com.dongguo.dianping.support.threadlocal.UserThreadLocalCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Administrator
 * @Date: 2024-03-19
 */
@RestController
@RequestMapping("/user")
@Tag(
        name = "UserController",
        description = "用户控制器接口")
public class UserController {

    @Resource
    private IUserService userService;
    @Resource
    private IUserInfoService userInfoService;
    @Operation(
            summary = "sendCode",
            description = "发送手机验证码"
    )
    @PostMapping("/sendCode")
    public Result sendCode(@RequestParam(value = "phone") String phone, HttpSession session) {
        return userService.sendCode(phone, session);
    }

    /**
     * 登录功能  目前只实现手机+验证码登录即可
     *
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    @Operation(
            summary = "login",
            description = "登录功能"
    )
    public Result login(@RequestBody LoginFormBO loginForm, HttpSession session) {
        return userService.login(loginForm,session);
    }
    /**
     * 登出功能
     *
     * @return 无
     */
    @PostMapping("/logout")
    @Operation(
            summary = "logout",
            description = "登出功能"
    )
    public Result logout(HttpServletRequest request) {
        return userService.logout(request);
    }
    /**
     * 请求接口时，拦截器会将session中的用户信息放入到ThreadLocal中
     * 所以直接从ThreadLocal中获取user信息即可。
     * @return
     */
    @GetMapping("/me")
    @Operation(
            summary = "me",
            description = "获取user信息"
    )
    public Result me() {
        UserDTO user = UserThreadLocalCache.getUser();
        return Result.ok(user);
    }

    @GetMapping("/info/{id}")
    @Operation(
            summary = "info",
            description = "获取user信息"
    )
    public Result info(@PathVariable("id") Long userId) {
        // 查询详情
        UserInfo info = userInfoService.getById(userId);
        if (info == null) {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        // 返回
        return Result.ok(info);
    }

    /**
     * 根据id查询用户
     * @param userId
     * @return
     */
    @Operation(
            summary = "queryUserById",
            description = "根据id查询用户"
    )
    @GetMapping("/{id}")
    public Result queryUserById(@PathVariable("id") Long userId){
        // 查询详情
        User user = userService.getById(userId);
        if (user == null) {
            return Result.ok();
        }
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        // 返回
        return Result.ok(userDTO);
    }
    @Operation(
            summary = "sign",
            description = "签到"
    )
    @PostMapping("/sign")
    public Result sign(){
        return userService.sign();
    }
    @Operation(
            summary = "signCount",
            description = "签到统计"
    )
    @GetMapping("/sign/count")
    public Result signCount(){
        return userService.signCount();
    }
}
