package com.dongguo.redis.entity.POJO;

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
 *  商户
 * </p>
 *
 */
@Schema(title = "Shop", description = "商户表")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_shop")
public class Shop implements Serializable {

    @Serial
    private static final long serialVersionUID = -7790411172934554883L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(name = "id", description = "ID")
    private Long id;

    /**
     * 商铺名称
     */
    @Schema(name = "name", description = "商铺名称")
    private String name;

    /**
     * 商铺类型的id
     */
    @Schema(name = "typeId", description = "商铺类型的id")
    private Long typeId;

    /**
     * 商铺图片，多个图片以','隔开
     */
    @Schema(name = "images", description = "商铺图片")
    private String images;

    /**
     * 商圈，例如陆家嘴
     */
    @Schema(name = "area", description = "商圈")
    private String area;

    /**
     * 地址
     */
    @Schema(name = "address", description = "地址")
    private String address;

    /**
     * 经度
     */
    @Schema(name = "x", description = "经度")
    private Double x;

    /**
     * 维度
     */
    @Schema(name = "y", description = "维度")
    private Double y;

    /**
     * 均价，取整数
     */
    @Schema(name = "avgPrice", description = "均价")
    private Long avgPrice;

    /**
     * 销量
     */
    @Schema(name = "sold", description = "销量")
    private Integer sold;

    /**
     * 评论数量
     */
    @Schema(name = "comments", description = "评论数量")
    private Integer comments;

    /**
     * 评分，1~5分，乘10保存，避免小数
     */
    @Schema(name = "score", description = "评分")
    private Integer score;

    /**
     * 营业时间，例如 10:00-22:00
     */
    @Schema(name = "openHours", description = "营业时间")
    private String openHours;

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


    @TableField(exist = false)
    private Double distance;
}
