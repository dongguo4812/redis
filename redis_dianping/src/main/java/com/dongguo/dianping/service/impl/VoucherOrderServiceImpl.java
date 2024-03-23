package com.dongguo.dianping.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.service.ISeckillVoucherService;
import com.dongguo.dianping.service.IVoucherOrderService;
import com.dongguo.dianping.support.redis.DistributedLockFactory;
import com.dongguo.dianping.utils.SnowflakeIdUtil;
import com.dongguo.dianping.entity.POJO.SeckillVoucher;
import com.dongguo.dianping.entity.POJO.VoucherOrder;
import com.dongguo.dianping.mapper.VoucherOrderMapper;
import com.dongguo.dianping.support.threadlocal.UserThreadLocalCache;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

import static com.dongguo.dianping.utils.RedisConstants.LOCK_VOUCHER_ORDER_KEY;

/**
 * <p>
 * 服务实现类
 * </p>
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    @Autowired
    private ISeckillVoucherService seckillVoucherService;

    /**
     * @param seckillVoucher
     * @return
     */
    @Transactional
    @Override
    public Result createVoucherOrder(SeckillVoucher seckillVoucher, Long userId) {
        Long voucherId = seckillVoucher.getVoucherId();
        //3判断库存 进行扣减库存
        Integer stock = seckillVoucher.getStock();
        if (stock < 1) {
            return Result.fail("该优惠券已被抢光");
        }
        //查询该用户是否抢购过这个优惠券

        Long count = lambdaQuery().select().eq(VoucherOrder::getVoucherId, voucherId)
                .eq(VoucherOrder::getUserId, userId)
                .count();
        if (count > 0) {
            return Result.fail("该优惠券一人只能抢购一个");
        }
        boolean result = seckillVoucherService.lambdaUpdate().setSql("stock = stock -1")
                .eq(SeckillVoucher::getVoucherId, voucherId)
                .gt(SeckillVoucher::getStock, 0)
                .update();
        if (!result) {
            return Result.fail("该优惠券库存扣减失败");
        }
        //4.创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        // 4.1.订单id
        long orderId = SnowflakeIdUtil.getNextId();
        voucherOrder.setId(orderId);
        // 4.2.用户id
        voucherOrder.setUserId(userId);
        // 4.3.代金券id
        voucherOrder.setVoucherId(voucherId);
        //订单状态默认生成为未支付 可以不写
        voucherOrder.setStatus(1);
        save(voucherOrder);
        return Result.ok(orderId);
    }

    /**
     * synchronized单机锁
     *
     * @param seckillVoucher
     * @return
     */
    @Transactional
    @Override
    public synchronized Result createVoucherOrderV1(SeckillVoucher seckillVoucher) {
        Long voucherId = seckillVoucher.getVoucherId();
        //3判断库存 进行扣减库存
        Integer stock = seckillVoucher.getStock();
        if (stock < 1) {
            return Result.fail("该优惠券已被抢光");
        }
        //查询该用户是否抢购过这个优惠券
        Long userId = UserThreadLocalCache.getUser().getId();

        Long count = lambdaQuery().select().eq(VoucherOrder::getVoucherId, voucherId)
                .eq(VoucherOrder::getUserId, userId)
                .count();
        if (count > 0) {
            return Result.fail("该优惠券一人只能抢购一个");
        }
        boolean result = seckillVoucherService.lambdaUpdate().setSql("stock = stock -1")
                .eq(SeckillVoucher::getVoucherId, voucherId)
                .gt(SeckillVoucher::getStock, 0)
                .update();
        if (!result) {
            return Result.fail("该优惠券库存扣减失败");
        }
        //4.创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        // 4.1.订单id
        long orderId = SnowflakeIdUtil.getNextId();
        voucherOrder.setId(orderId);
        // 4.2.用户id
        voucherOrder.setUserId(userId);
        // 4.3.代金券id
        voucherOrder.setVoucherId(voucherId);
        //订单状态默认生成为未支付 可以不写
        voucherOrder.setStatus(1);
        save(voucherOrder);
        return Result.ok(orderId);
    }



    @Override
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        //查询该用户是否抢购过这个优惠券
        Long userId = voucherOrder.getUserId();
        Long voucherId = voucherOrder.getVoucherId();
        Long count = lambdaQuery().select().eq(VoucherOrder::getVoucherId, voucherId)
                .eq(VoucherOrder::getUserId, userId)
                .count();
        if (count > 0) {
            log.error("用户已经购买过一次");
            return;
        }

        boolean result = seckillVoucherService.lambdaUpdate().setSql("stock = stock -1")
                .eq(SeckillVoucher::getVoucherId, voucherId)
                .gt(SeckillVoucher::getStock, 0)
                .update();
        if (!result) {
            log.error("库存不足");
            return;
        }
        save(voucherOrder);
    }
}
