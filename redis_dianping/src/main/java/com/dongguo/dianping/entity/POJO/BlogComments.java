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
@TableName("tb_blog_comments")
@Schema(title = "BlogComments", description = "笔记评价表")
public class BlogComments implements Serializable {


    @Serial
    private static final long serialVersionUID = -4458931670306607343L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(name = "id", description = "ID")
    private Long id;

    /**
     * 用户id
     */
    @Schema(name = "userId", description = "用户id")
    private Long userId;

    /**
     * 探店id
     */
    @Schema(name = "blogId", description = "探店id")
    private Long blogId;

    /**
     * 关联的1级评论id，如果是一级评论，则值为0
     */
    @Schema(name = "parentId", description = "关联的1级评论id")
    private Long parentId;

    /**
     * 回复的评论id
     */
    @Schema(name = "answerId", description = "回复的评论id")
    private Long answerId;

    /**
     * 回复的内容
     */
    @Schema(name = "content", description = "回复的内容")
    private String content;

    /**
     * 点赞数
     */
    @Schema(name = "liked", description = "点赞数")
    private Integer liked;

    /**
     * 状态，0：正常，1：被举报，2：禁止查看
     */
    @Schema(name = "status", description = "状态")
    private Boolean status;

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
