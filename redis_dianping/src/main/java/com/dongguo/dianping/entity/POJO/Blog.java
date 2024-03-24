package com.dongguo.dianping.entity.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_blog")
@Schema(title = "Blog", description = "笔记表")
public class Blog implements Serializable {


    @Serial
    private static final long serialVersionUID = 3676402306687171172L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(name = "id", description = "ID")
    private Long id;
    /**
     * 商户id
     */
    @Schema(name = "shopId", description = "商户id")
    private Long shopId;
    /**
     * 用户id
     */
    @Schema(name = "userId", description = "用户id")
    private Long userId;
    /**
     * 用户图标
     */
    @Schema(name = "icon", description = "用户图标")
    @TableField(exist = false)
    private String icon;
    /**
     * 用户姓名
     */
    @Schema(name = "name", description = "用户姓名")
    @TableField(exist = false)
    private String name;
    /**
     * 是否点赞过了
     */
    @Schema(name = "isLike", description = "是否点赞过了")
    @TableField(exist = false)
    private Boolean isLike;

    /**
     * 标题
     */
    @Schema(name = "title", description = "标题")
    private String title;

    /**
     * 探店的照片，最多9张，多张以","隔开
     */
    @Schema(name = "images", description = "探店的照片")
    private String images;

    /**
     * 探店的文字描述
     */
    @Schema(name = "content", description = "探店的文字描述")
    private String content;

    /**
     * 点赞数量
     */
    @Schema(name = "liked", description = "点赞数量")
    private Integer liked;

    /**
     * 评论数量
     */
    @Schema(name = "comments", description = "评论数量")
    private Integer comments;

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
