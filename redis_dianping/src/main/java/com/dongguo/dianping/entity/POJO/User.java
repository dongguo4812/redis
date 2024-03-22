package com.dongguo.dianping.entity.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *  用户表
 * </p>
 *
 */
@Schema(title = "User", description = "用户表")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 3953415524029149087L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(name = "id", description = "ID")
    private Long id;

    /**
     * 手机号码
     */
    @Schema(name = "phone", description = "手机号码")
    private String phone;

    /**
     * 密码，加密存储
     */
    @Schema(name = "password", description = "密码")
    private String password;

    /**
     * 昵称，默认是随机字符
     */
    @Schema(name = "昵称", description = "nickName")
    private String nickName;

    /**
     * 用户头像
     */
    @Schema(name = "icon", description = "用户头像")
    private String icon = "";

    /**
     * 创建时间
     */
    @Schema(name = "createTime", description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(name = "updateTime", description = "更新时间")
    private LocalDateTime updateTime;
}
