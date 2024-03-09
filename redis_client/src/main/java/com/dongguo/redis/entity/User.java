package com.dongguo.redis.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Schema(title = "用户表", description = "用户表")
@Table(name = "t_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(generator = "JDBC")
    @Schema(name = "ID", description = "ID")
    private Long id;

    /**
     * 用户名
     */
    @Schema(name = "用户名", description = "用户名")
    private String username;

    /**
     * 密码
     */
    @Schema(name = "密码", description = "密码")
    private String password;

    /**
     * 性别 0=女 1=男 
     */
    @Schema(name = "性别", description = "性别 0=女 1=男")
    private Byte sex;

    /**
     * 删除标志，默认0不删除，1删除
     */
    @Schema(name = "删除标志", description = "删除标志，默认0不删除，1删除")
    private Byte deleted;

    /**
     * 更新时间
     */
    @Schema(name = "更新时间", description = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 创建时间
     */
    @Schema(name = "创建时间", description = "创建时间")
    @Column(name = "create_time")
    private Date createTime;
}