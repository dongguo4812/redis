package com.dongguo.dianping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.entity.POJO.SeckillVoucher;
import com.dongguo.dianping.entity.POJO.VoucherOrder;


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
