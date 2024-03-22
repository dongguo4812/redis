package com.dongguo.dianping.entity.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("tb_voucher")
@Schema(title = "Voucher", description = "优惠券表")
public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(name = "id", description = "ID")
    private Long id;

    /**
     * 商铺id
     */
    @Schema(name = "shopId", description = "商铺id")
    private Long shopId;

    /**
     * 代金券标题
     */
    @Schema(name = "title", description = "代金券标题")
    private String title;

    /**
     * 副标题
     */
    @Schema(name = "subTitle", description = "副标题")
    private String subTitle;

    /**
     * 使用规则
     */
    @Schema(name = "rules", description = "使用规则")
    private String rules;

    /**
     * 支付金额
     */
    @Schema(name = "payValue", description = "支付金额")
    private Long payValue;

    /**
     * 抵扣金额
     */
    @Schema(name = "actualValue", description = "抵扣金额")
    private Long actualValue;

    /**
     * 优惠券类型
     */
    @Schema(name = "type", description = "优惠券类型")
    private Integer type;

    /**
     * 优惠券状态
     */
    @Schema(name = "status", description = "优惠券状态")
    private Integer status;
    /**
     * 库存
     */
    @TableField(exist = false)
    @Schema(name = "stock", description = "库存")
    private Integer stock;

    /**
     * 生效时间
     */
    @TableField(exist = false)
    @Schema(name = "beginTime", description = "生效时间")
    private Date beginTime;

    /**
     * 失效时间
     */
    @TableField(exist = false)
    @Schema(name = "endTime", description = "失效时间")
    private Date endTime;

    /**
     * 创建时间
     */
    @Schema(name = "updateTime", description = "创建时间")
    private Date createTime;


    /**
     * 更新时间
     */
    @Schema(name = "updateTime", description = "更新时间")
    private Date updateTime;
}
