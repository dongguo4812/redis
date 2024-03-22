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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user_info")
@Schema(title = "UserInfo", description = "用户信息表")
public class UserInfo implements Serializable {


    @Serial
    private static final long serialVersionUID = -8301002550086419993L;
    /**
     * 主键，用户id
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    @Schema(name = "userId", description = "用户id")
    private Long userId;

    /**
     * 城市名称
     */
    @Schema(name = "city", description = "城市名称")
    private String city;

    /**
     * 个人介绍，不要超过128个字符
     */
    @Schema(name = "introduce", description = "个人介绍")
    private String introduce;

    /**
     * 粉丝数量
     */
    @Schema(name = "fans", description = "粉丝数量")
    private Integer fans;

    /**
     * 关注的人的数量
     */
    @Schema(name = "follower", description = "关注的人的数量")
    private Integer follower;

    /**
     * 性别，0：男，1：女
     */
    @Schema(name = "gender", description = "性别")
    private Boolean gender;

    /**
     * 生日
     */
    @Schema(name = "birthday", description = "生日")
    private LocalDate birthday;

    /**
     * 积分
     */
    @Schema(name = "credits", description = "积分")
    private Integer credits;

    /**
     * 会员级别，0~9级,0代表未开通会员
     */
    @Schema(name = "level", description = "会员级别")
    private Boolean level;

    /**
     * 创建时间
     */
    @Schema(name = "createTime", description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(name = "updateTime", description = "更新时间")
    private Date updateTime;
}
