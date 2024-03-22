package com.dongguo.dianping.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.service.ISeckillVoucherService;
import com.dongguo.dianping.service.IVoucherOrderService;
import com.dongguo.dianping.utils.SnowflakeIdUtil;
import com.dongguo.dianping.entity.POJO.SeckillVoucher;
import com.dongguo.dianping.entity.POJO.VoucherOrder;
import com.dongguo.dianping.mapper.VoucherOrderMapper;
import com.dongguo.dianping.support.threadlocal.UserThreadLocalCache;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 服务实现类
 * </p>
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    @Autowired
    private ISeckillVoucherService seckillVoucherService;
    @Autowired
    private IVoucherOrderService voucherOrderService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    private IVoucherOrderService proxy;
    private static final String CLASS_PATH = "/redis/seckill.lua";
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource(CLASS_PATH));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

//    private BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024 * 1024);

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
                    handleVoucherOrder(voucherOrder);
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
                    createVoucherOrder(voucherOrder);
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
//    private class VoucherOrderHandler implements Runnable {
//
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    // 1.获取队列中的订单信息
//                    VoucherOrder voucherOrder = orderTasks.take();
//                    // 2.创建订单
//                    handleVoucherOrder(voucherOrder);
//                } catch (Exception e) {
//                    log.error("处理订单异常", e);
//                }
//            }
//        }
//    }

    /**
     * 以防万一 仍然加锁
     *
     * @param voucherOrder
     */
    private void handleVoucherOrder(VoucherOrder voucherOrder) {
        //1.获取用户
        Long userId = voucherOrder.getUserId();
        // 2.创建锁对象
        RLock redisLock = redissonClient.getLock("lock:order:" + userId);
        // 3.尝试获取锁
        boolean isLock = redisLock.tryLock();
        // 4.判断是否获得锁成功
        if (!isLock) {
            // 获取锁失败，直接返回失败或者重试
            log.error("不允许重复下单！");
            return;
        }
        try {
            //注意：由于是spring的事务是放在threadLocal中，此时的是多线程，事务会失效
            proxy.createVoucherOrder(voucherOrder);
        } finally {
            // 释放锁
            redisLock.unlock();
        }
    }

//    @Override
//    public Result seckillVoucher(Long voucherId) {
//        Long userId = UserThreadLocalCache.getUser().getId();
//        //返回订单id
//        long orderId = SnowflakeIdUtil.getNextId();
//        //1执行lua脚本
//        Long result = stringRedisTemplate.execute(SECKILL_SCRIPT,
//                Collections.emptyList(),
//                //lua脚本传的是字符串
//                voucherId.toString(), userId.toString(), String.valueOf(orderId));
//        //判断结果是否为0  第一次购买
//        int r = result.intValue();
//        if (r != 0) {
//            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
//        }
//        //获取代理对象
//        proxy = (IVoucherOrderService) AopContext.currentProxy();
//
//        return Result.ok(orderId);
//    }

    @Override
    @Transactional
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
        //3判断库存 进行扣减库存
        Integer stock = seckillVoucher.getStock();
        if (stock < 1) {
            return Result.fail("该优惠券已被抢光");
        }
        //查询该用户是否抢购过这个优惠券
        Long userId = UserThreadLocalCache.getUser().getId();

        Long count = voucherOrderService.lambdaQuery().select().eq(VoucherOrder::getVoucherId, voucherId)
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
//    Long userId = UserThreadLocalCache.getUser().getId();
//    //        SimpleRedisLock lock = new SimpleRedisLock("order:" + userId, stringRedisTemplate);
////        boolean isLock = lock.tryLock(1500);
//    RLock lock = redissonClient.getLock("lock:order:" + userId);
//    //参数分别是：获取锁的最大等待时间(期间会重试)，锁自动释放时间，时间单位
//    boolean isLock = lock.tryLock();
//        if (!isLock) {
//        return Result.fail("该优惠券一个用户只能使用一次");
//    }
//        try {
////        synchronized (userId.toString().intern()) {
////            Result result = createVoucherOrder(seckillVoucher);
//
//        IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
//        return proxy.createVoucherOrder(seckillVoucher);
//    } finally {
//        lock.unlock();
//    }

    //        }
    @Transactional
    @Override
    public Result createVoucherOrder(SeckillVoucher seckillVoucher) {
        Long voucherId = seckillVoucher.getVoucherId();
        //3判断库存 进行扣减库存
        Integer stock = seckillVoucher.getStock();
        if (stock < 1) {
            return Result.fail("该优惠券已被抢光");
        }
        //查询该用户是否抢购过这个优惠券
        Long userId = UserThreadLocalCache.getUser().getId();

        Long count = voucherOrderService.lambdaQuery().select().eq(VoucherOrder::getVoucherId, voucherId)
                .eq(VoucherOrder::getUserId, userId)
                .count();
        if (count > 0) {
            return Result.fail("该优惠券一人只能抢购一个");
        }
//        boolean result = seckillVoucherService.update().setSql("stock = stock -1")
//                .eq("voucher_id", voucherId)
//                .gt("stock", 0)
//                .update();

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
        save(voucherOrder);
        //订单状态默认生成为未支付
        return Result.ok(orderId);

    }


    @Override
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        //查询该用户是否抢购过这个优惠券
        Long userId = voucherOrder.getUserId();
        Long voucherId = voucherOrder.getVoucherId();
        Long count = voucherOrderService.lambdaQuery().select().eq(VoucherOrder::getVoucherId, voucherId)
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
