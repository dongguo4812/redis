package com.dongguo.redis.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(title = "用户信息", description = "用户信息")
public class UserDTO implements Serializable {
    @Schema(name = "ID", description = "ID")
    private Long id;

    @Schema(name = "用户名", description = "用户名")
    private String username;

    @Schema(name = "密码", description = "密码")
    private String password;

    @Schema(name = "性别", description = "性别 0=女 1=男")
    private Byte sex;

    @Schema(name = "删除标志", description = "删除标志，默认0不删除，1删除")
    private Byte deleted;

    @Schema(name = "更新时间", description = "更新时间")
    private Date updateTime;

    @Schema(name = "创建时间", description = "创建时间")
    private Date createTime;
}