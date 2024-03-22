package com.dongguo.dianping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.entity.POJO.Voucher;


/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IVoucherService extends IService<Voucher> {

    Result queryVoucherOfShop(Long shopId);

    void addSeckillVoucher(Voucher voucher);
}
