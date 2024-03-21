package com.dongguo.redis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongguo.redis.entity.POJO.SeckillVoucher;
import com.dongguo.redis.entity.POJO.VoucherOrder;
import com.dongguo.redis.entity.Result;


/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    Result seckillVoucher(Long voucherId);

    Result createVoucherOrder(SeckillVoucher seckillVoucher);

    void createVoucherOrder(VoucherOrder voucherOrder);
}
