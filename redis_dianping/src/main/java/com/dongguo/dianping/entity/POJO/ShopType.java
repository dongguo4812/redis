package com.dongguo.dianping.entity.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * 店铺类型
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_shop_type")
@Schema(title = "ShopType", description = "店铺类型表")
public class ShopType implements Serializable {


    @Serial
    private static final long serialVersionUID = 943809799805846999L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(name = "id", description = "ID")
    private Long id;

    /**
     * 类型名称
     */
    @Schema(name = "name", description = "类型名称")
    private String name;

    /**
     * 图标
     */
    @Schema(name = "icon", description = "图标")
    private String icon;

    /**
     * 顺序
     */
    @Schema(name = "sort", description = "顺序")
    private Integer sort;

    /**
     * 创建时间
     */
    @JsonIgnore
    @Schema(name = "createTime", description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonIgnore
    @Schema(name = "updateTime", description = "更新时间")
    private Date updateTime;
}
