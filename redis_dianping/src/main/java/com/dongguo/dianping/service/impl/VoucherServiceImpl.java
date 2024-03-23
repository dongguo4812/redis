package com.dongguo.dianping.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dongguo.dianping.entity.POJO.VoucherOrder;
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
import com.dongguo.dianping.utils.SnowflakeIdUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    /**
     * redis分布式锁
     */
    @Resource
    private DistributedLockFactory distributedLockFactory;

    @Override
    public Result seckillVoucherV1(Long voucherId) {
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

    /**
     * redisson分布式锁
     */
    @Resource
    private RedissonClient redissonClient1;
    @Resource
    private RedissonClient redissonClient2;
    @Resource
    private RedissonClient redissonClient3;

    @Override
    public Result seckillVoucherV2(Long voucherId) {
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
        String key = LOCK_VOUCHER_ORDER_KEY + userId + ":" + voucherId;
        RLock lock1 = redissonClient1.getLock(key);
        RLock lock2 = redissonClient2.getLock(key);
        RLock lock3 = redissonClient3.getLock(key);
        RedissonRedLock redLock = new RedissonRedLock(lock1, lock2, lock3);
        redLock.lock();
        Result result;
        try {
            result = voucherOrderService.createVoucherOrder(seckillVoucher, userId);
        } finally {
            redLock.unlock();
        }
        return result;
    }


    private static final String CLASS_PATH = "redis/seckill.lua";
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource(CLASS_PATH));
        SECKILL_SCRIPT.setResultType(Long.class);
    }
    @Override
    public Result seckillVoucher(Long voucherId) {
        Long userId = UserThreadLocalCache.getUser().getId();
        //返回订单id
        long orderId = SnowflakeIdUtil.getNextId();
        //1执行lua脚本
        Long result = stringRedisTemplate.execute(SECKILL_SCRIPT,
                Collections.emptyList(),
                //lua脚本传的是字符串
                voucherId.toString(), userId.toString(), String.valueOf(orderId));
        //判断结果是否为0  第一次购买
        int r = result.intValue();
        if (r != 0) {
            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
        }
        return Result.ok(userId);
    }

    /**
     * stream消息队列
     */

    //异步处理线程池
    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

    //在类初始化之后执行，因为当这个类初始化好了之后，随时都是有可能要执行的
    @PostConstruct
    private void init() {
        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
    }

    private class VoucherOrderHandler implements Runnable {
        String queueName = "stream.orders";

        @Override
        public void run() {
            while (true) {
                try {
                    // 1.获取消息队列中的订单信息
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(queueName, ReadOffset.lastConsumed())
                    );
                    if (CollUtil.isEmpty(list)) {
                        continue;
                    }
                    //解析消息 获取订单
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
                    // 2.创建订单
                    voucherOrderService.createVoucherOrder(voucherOrder);
                    //返回ack
                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", record.getId());
                } catch (Exception e) {
                    log.error("处理订单异常", e);
                    //处理异常消息
                    handlePendingList();
                }
            }
        }

        private void handlePendingList() {
            while (true) {
                try {
                    // 1.获取pending-list中的订单信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS s1 0
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(queueName, ReadOffset.from("0"))
                    );
                    // 2.判断订单信息是否为空
                    if (list == null || list.isEmpty()) {
                        // 如果为null，说明没有异常消息，结束循环
                        break;
                    }
                    // 解析数据
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
                    // 3.创建订单
                    voucherOrderService.createVoucherOrder(voucherOrder);
                    // 4.确认消息 XACK
                    stringRedisTemplate.opsForStream().acknowledge("s1", "g1", record.getId());
                } catch (Exception e) {
                    log.error("处理pendding订单异常", e);
                    try {
                        Thread.sleep(20);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
