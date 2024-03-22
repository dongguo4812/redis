package com.dongguo.dianping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.mapper.VoucherMapper;
import com.dongguo.dianping.service.ISeckillVoucherService;
import com.dongguo.dianping.service.IVoucherOrderService;
import com.dongguo.dianping.service.IVoucherService;
import com.dongguo.dianping.support.redis.DistributedLockFactory;
import com.dongguo.dianping.support.threadlocal.UserThreadLocalCache;
import com.dongguo.dianping.utils.RedisConstants;
import com.dongguo.dianping.entity.POJO.SeckillVoucher;
import com.dongguo.dianping.entity.POJO.Voucher;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;

import static com.dongguo.dianping.utils.RedisConstants.LOCK_VOUCHER_ORDER_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 */
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private IVoucherOrderService voucherOrderService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryVoucherOfShop(Long shopId) {
        // 查询优惠券信息
        List<Voucher> vouchers = getBaseMapper().queryVoucherOfShop(shopId);
        // 返回结果
        return Result.ok(vouchers);
    }

    @Override
    @Transactional
    public void addSeckillVoucher(Voucher voucher) {
        // 保存优惠券
        save(voucher);
        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        seckillVoucherService.save(seckillVoucher);

        //将优惠券库存缓存到redis中
        stringRedisTemplate.opsForValue().set(RedisConstants.SECKILL_STOCK_KEY + voucher.getId(), voucher.getStock().toString());
    }
    @Resource
    private DistributedLockFactory distributedLockFactory;
    @Override
    public Result seckillVoucher(Long voucherId) {
        //查询优惠券
        SeckillVoucher seckillVoucher = seckillVoucherService.getById(voucherId);
        if (seckillVoucher == null) {
            return Result.fail("优惠券不存在");
        }
        //2判断优惠时间
        Date beginTime = seckillVoucher.getBeginTime();
        Date endTime = seckillVoucher.getEndTime();
        Date now = new Date();
        if (now.before(beginTime)) {
            return Result.fail("该优惠券秒杀时间尚未开始");
        }
        if (now.after(endTime)) {
            return Result.fail("该优惠券秒杀时间已过期");
        }
        Long userId = UserThreadLocalCache.getUser().getId();
        Lock lock = distributedLockFactory.getDistributedLock("redis", LOCK_VOUCHER_ORDER_KEY + userId + ":" + voucherId);
        lock.lock();
        Result result;
        try {
            result = voucherOrderService.createVoucherOrder(seckillVoucher, userId);
        } finally {
            lock.unlock();
        }
        return result;
    }
}
