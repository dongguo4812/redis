package com.dongguo.redis.controller;

import com.dongguo.redis.entity.User;
import com.dongguo.redis.entity.UserBO;
import com.dongguo.redis.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(
        name = "UserController",
        description = "用户控制器接口")
public class UserController {
    @Resource
    private UserService userService;

    /***
     * 根据ID查询用户信息（单条）
     * @param id 用户ID
     * @return 返回一条数据
     */
    @Operation(
            summary = "根据Id查询用户信息",
            description = "根据ID查询用户信息，并返回响应结果信息",
            parameters = {
                    @Parameter(name = "id", description = "用户ID", required = true, example = "1")
            }
    )
    @GetMapping("/findUser/{id}")
    public User findUser(@PathVariable(value = "id") Long id) {
        return userService.findUser(id);
    }

    /***
     * 新增用户信息（单条）
     */
    @Operation(
            summary = "新增用户信息",
            description = "新增用户信息"
    )
    @PostMapping("/addUser")
    public void addUser(@RequestBody UserBO userBO) {
        User user = new User();
        BeanUtils.copyProperties(userBO, user);
        userService.addUser(user);
    }

    /***
     * 删除用户信息（单条）
     */
    @Operation(
            summary = "删除用户信息",
            description = "删除用户信息"
    )
    @PostMapping("/deleteUser/{id}")
    public void deleteUser(@PathVariable(value = "id") Long id) {
        userService.deleteUser(id);
    }

    /***
     * 编辑用户信息（单条）
     */
    @Operation(
            summary = "编辑用户信息",
            description = "编辑用户信息"
    )
    @PostMapping("/editUser")
    public void editUser(@RequestBody UserBO userBO) {
        User user = new User();
        BeanUtils.copyProperties(userBO, user);
        userService.editUser(user);
    }
}
