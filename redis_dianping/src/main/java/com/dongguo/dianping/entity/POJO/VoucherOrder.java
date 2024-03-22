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
@TableName("tb_voucher_order")
@Schema(title = "VoucherOrder", description = "优惠券订单表")
public class VoucherOrder implements Serializable {


    @Serial
    private static final long serialVersionUID = -7086990158201618402L;
    /**
     * 主键
     */
    @Schema(name = "id", description = "ID")
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 下单的用户id
     */
    @Schema(name = "userId", description = "下单的用户id")
    private Long userId;

    /**
     * 购买的代金券id
     */
    @Schema(name = "voucherId", description = "购买的代金券id")
    private Long voucherId;

    /**
     * 支付方式 1：余额支付；2：支付宝；3：微信
     */
    @Schema(name = "payType", description = "支付方式")
    private Integer payType;

    /**
     * 订单状态，1：未支付；2：已支付；3：已核销；4：已取消；5：退款中；6：已退款
     */
    @Schema(name = "status", description = "订单状态")
    private Integer status;

    /**
     * 下单时间
     */
    @Schema(name = "createTime", description = "下单时间")
    private Date createTime;

    /**
     * 支付时间
     */
    @Schema(name = "payTime", description = "支付时间")
    private Date payTime;

    /**
     * 核销时间
     */
    @Schema(name = "useTime", description = "核销时间")
    private Date useTime;

    /**
     * 退款时间
     */
    @Schema(name = "refundTime", description = "退款时间")
    private Date refundTime;

    /**
     * 更新时间
     */
    @Schema(name = "updateTime", description = "更新时间")
    private Date updateTime;


}
