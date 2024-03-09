package com.dongguo.redis.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "用户信息", description = "用户信息")
public class UserBO implements Serializable {
    @Schema(name = "id", description = "ID")
    private Long id;

    @Schema(name = "username", description = "用户名")
    private String username;

    @Schema(name = "password", description = "密码")
    private String password;

    @Schema(name = "sex", description = "性别 0=女 1=男")
    private Byte sex;

    @Schema(name = "deleted", description = "删除标志，默认0不删除，1删除")
    private Byte deleted;

    @Schema(name = "updateTime", description = "更新时间")
    private Date updateTime;

    @Schema(name = "createTime", description = "创建时间")
    private Date createTime;
}