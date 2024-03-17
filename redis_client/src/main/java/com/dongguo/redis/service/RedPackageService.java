package com.dongguo.redis.service;

import cn.hutool.core.util.IdUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.lang.Long;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_RED_PACKAGE_CONSUME_KEY;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_RED_PACKAGE_KEY;

@Service
@Slf4j
public class RedPackageService {
    @Resource
    private RedisTemplate redisTemplate;


    public String send(Long totalMoney, Integer redPackageCount) {
        //先拆分红包
        Long[] splitRedPackage = splitRedPackage(totalMoney, redPackageCount);

        //list保存红包
        String redPackageKey = IdUtil.simpleUUID();
        String key = CACHE_RED_PACKAGE_KEY + redPackageKey;
        redisTemplate.opsForList().leftPushAll(key, splitRedPackage);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
        return redPackageKey + Arrays.toString(Arrays.stream(splitRedPackage)
                .mapToLong(Long::valueOf)
                .toArray());
    }

    /**
     * 红包金额二倍均值算法
     *
     * @param totalMoney
     * @param redPackageCount
     * @return
     */
    private Long[] splitRedPackage(Long totalMoney, Integer redPackageCount) {
        //已经划分的总金额
        Long useMoney = 0L;
        //保存每个红包划分的金额
        Long[] redPackageMoney = new Long[redPackageCount];
        Random random = new Random();
        for (int i = 0; i < redPackageCount; i++) {
            if (i == redPackageCount - 1) {
                //最后一个红包
                redPackageMoney[i] = totalMoney - useMoney;
            } else {
                //二倍均值法 获取最大值
                Long unUseMoney = totalMoney - useMoney;
                Long avgMoney = (unUseMoney / redPackageCount - i) * 2;
                //生成一个在 1 到 avgMoney - 1 之间的随机整数。
                redPackageMoney[i] = 1 + random.nextLong(avgMoney - 1);
            }
            useMoney = useMoney + redPackageMoney[i];
        }
        return redPackageMoney;
    }


    public String grab(String redPackageKey, String userId) {
        Boolean hasKey = redisTemplate.opsForHash().hasKey(CACHE_RED_PACKAGE_CONSUME_KEY + redPackageKey, userId);
        //没有抢过
        if (!hasKey) {
            Object redPackage = redisTemplate.opsForList().leftPop(CACHE_RED_PACKAGE_KEY + redPackageKey);
            if (null != redPackage) {
                //抢红包成功，进行记录
                redisTemplate.opsForHash().put(CACHE_RED_PACKAGE_CONSUME_KEY + redPackageKey, userId, redPackage);
                log.info("用户id:" + userId + " 抢到红包，红包金额：" + redPackage);
                //后续可通过消息队列异步到mysql
                return "用户id:" + userId + " 抢到红包，红包金额：" + redPackage;
            } else {
                //抢红包失败
                return "抢红包失败，红包已被抢完了!";
            }
        }
        return "抢红包失败，用户ID：" + userId + "已经抢过红包!";
    }
}
