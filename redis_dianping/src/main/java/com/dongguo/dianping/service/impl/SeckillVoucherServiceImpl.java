package com.dongguo.dianping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongguo.dianping.service.ISeckillVoucherService;
import com.dongguo.dianping.entity.POJO.SeckillVoucher;
import com.dongguo.dianping.mapper.SeckillVoucherMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 秒杀优惠券表，与优惠券是一对一关系 服务实现类
 * </p>
 *
 */
@Service
public class SeckillVoucherServiceImpl extends ServiceImpl<SeckillVoucherMapper, SeckillVoucher> implements ISeckillVoucherService {

}
