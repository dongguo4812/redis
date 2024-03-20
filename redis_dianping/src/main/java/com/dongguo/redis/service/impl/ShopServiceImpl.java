package com.dongguo.redis.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongguo.redis.entity.POJO.Shop;
import com.dongguo.redis.entity.Result;
import com.dongguo.redis.mapper.ShopMapper;
import com.dongguo.redis.service.IShopService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.dongguo.redis.utils.RedisConstants.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    @Resource
    private RedisTemplate redisTemplate;
    @Autowired
    private ShopMapper shopMapper;

    @Override
    public Result queryShopById(Long id) {
        if (id == null) {
            return Result.fail("店铺id不能为空");
        }
        String shopCacheKey = CACHE_SHOP_KEY + id;
        //先查缓存
        Map<Object, Object> map = redisTemplate.opsForHash().entries(shopCacheKey);
        //判空
        if (!map.isEmpty()) {
            //缓存查到 返回
            return Result.ok(BeanUtil.toBean(map, Shop.class));
        }
        //缓存中没查到  查数据库
        Shop shop = shopMapper.selectById(id);
        if (ObjectUtil.isEmpty(shop)) {
            return Result.fail("店铺信息不存在");
        }
        //查数据库 存到缓存中
        Map<Object, Object> shopMap = new HashMap<>();
        BeanUtil.copyProperties(shop, shopMap);
        redisTemplate.opsForHash().putAll(shopCacheKey, shopMap);
        redisTemplate.expire(shopCacheKey, CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return Result.ok(shop);
    }

//    public Shop saveShop2Redis(Long id, Long expireSeconds) {
//        //查询店铺数据
//        Shop shop = shopMapper.selectById(id);
//        if (ObjectUtil.isNotEmpty(shop)) {
//            RedisData redisData = new RedisData();
//            redisData.setData(shop);
//            redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
//            try {
//               Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
//
//        }
//        return shop;
//    }
//
//    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

//    /**
//     * 逻辑过期解决缓存击穿问题
//     *
//     * @param id
//     * @return
//     */
//    public Shop cacheShopWithLogicalExpire(Long id) {
//        String shopCacheKey = CACHE_SHOP_KEY + id;
//        String redisDataJson = stringRedisTemplate.opsForValue().get(shopCacheKey);
//        if (StrUtil.isBlank(redisDataJson)) {
//            return null;
//        }
//        RedisData redisData = JSONUtil.toBean(redisDataJson, RedisData.class);
//        JSONObject data = (JSONObject) redisData;
//        Shop shop = JSONUtil.toBean(data, Shop.class);
//        LocalDateTime expireTime = redisData.getExpireTime();
//        //判断是否过期
//        if (expireTime.isAfter(LocalDateTime.now())){
//            //没过期
//            return shop;
//        }
//        //过期了 重建缓存
//        boolean isLock = tryLock(RedisConstants.LOCK_SHOP_KEY + id);
//        if (isLock){
//            //开启独立线程 重建缓存
//            CACHE_REBUILD_EXECUTOR.submit(()->{
//                try {
//                    saveShop2Redis(id, RedisConstants.LOCK_SHOP_TTL);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }finally {
//                    unLock(RedisConstants.LOCK_SHOP_KEY + id);
//                }
//            });
//        }
//        return shop;
//    }
//
//    /**
//     * 互斥锁解决缓存雪崩
//     *
//     * @return
//     */
//    public Shop cacheShopWithMutex(Long id) {
//        String shopCacheKey = RedisConstants.CACHE_SHOP_KEY + id;
//        //先查缓存
//        String jsonShop = stringRedisTemplate.opsForValue().get(shopCacheKey);
//        if (StrUtil.isNotBlank(jsonShop)) {
//            return JSONUtil.toBean(jsonShop, Shop.class);
//        }
//        //缓存的空值   不是null 那就是我们缓存的空字符串""
//        if (jsonShop != null) {
//            return null;
//        }
//        String lockKey = RedisConstants.LOCK_SHOP_KEY + id;
//        //为空 获取锁 更新缓存
//        boolean result = tryLock(lockKey);
//        if (!result) {
//            try {
//                TimeUnit.MILLISECONDS.sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return cacheShopWithMutex(id);
//        }
//        //获取锁成功
//        Shop shop = shopMapper.selectById(id);
//        if (ObjectUtil.isEmpty(shop)) {
//            //解决缓存穿透问题  缓存空对象
//            stringRedisTemplate.opsForValue().set(shopCacheKey, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
//            return null;
//        }
//        stringRedisTemplate.opsForValue().set(shopCacheKey, JSONUtil.toJsonStr(shop), RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
//        unLock(lockKey);
//        return shop;
//    }
//
//    /**
//     * 获取锁
//     *
//     * @param key
//     * @return
//     */
//    public boolean tryLock(String key) {
//        Boolean ifAbsent = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.MINUTES);
//        return BooleanUtil.isTrue(ifAbsent);
//    }
//
//    /**
//     * 释放锁
//     *
//     * @param key
//     */
//    public void unLock(String key) {
//        stringRedisTemplate.delete(key);
//    }
//
//
    /**
     * 查询缓存   空值解决缓存穿透
     *
     * @param id
     * @return
     */
//    public Shop cacheShopWithPassThough(Long id) {
//        String shopCacheKey = CACHE_SHOP_KEY + id;
//        //先查缓存
//        String jsonShop = stringRedisTemplate.opsForValue().get(shopCacheKey);
//        //判空
//        if (StrUtil.isNotBlank(jsonShop)) {
//            //缓存查到 返回
//            return JSONUtil.toBean(jsonShop, Shop.class);
//        }
//        //缓存的空值   不是null 那就是我们缓存的空字符串""
//        if (jsonShop != null) {
//            return null;
//        }
//        //缓存中没查到  查数据库
//        Shop shop = shopMapper.selectById(id);
//        if (ObjectUtils.isEmpty(shop)) {
//            //解决缓存穿透问题  缓存空对象
//            stringRedisTemplate.opsForValue().set(shopCacheKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
//            return null;
//        }
//        //查数据库 存到缓存中
//        stringRedisTemplate.opsForValue().set(shopCacheKey, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
//        return shop;
//    }

//
//    @Override
//    @Transactional
//    public Result updateShop(Shop shop) {
//        Shop shopById = getById(shop.getId());
//        if (ObjectUtils.isEmpty(shopById)) {
//            return Result.fail("商铺不存在");
//        }
//        // 写入数据库
//        updateById(shop);
//
//        //删除缓存
//        stringRedisTemplate.delete(RedisConstants.CACHE_SHOP_KEY + shop.getId());
//        return Result.ok();
//    }
//
//    @Override
//    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {
//        // 1.判断是否需要根据坐标查询
//        if (x == null || y == null) {
//            // 不需要坐标查询，按数据库查询
//            Page<Shop> page = query()
//                    .eq("type_id", typeId)
//                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
//            // 返回数据
//            return Result.ok(page.getRecords());
//        }
//        // 2.计算分页参数
//        int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
//        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;
//        // 3.查询redis、按照距离排序、分页。结果：shopId、distance
//        String key = RedisConstants.SHOP_GEO_KEY + typeId;
//        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo() // GEOSEARCH key BYLONLAT x y BYRADIUS 10 WITHDISTANCE
//                .search(
//                        key,
//                        GeoReference.fromCoordinate(x, y),
//                        new Distance(5000),
//                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
//                );
//        // 4.解析出id
//        if (results == null) {
//            return Result.ok(Collections.emptyList());
//        }
//
//        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
//        if (list.size() <= from) {
//            // 没有下一页了，结束
//            return Result.ok(Collections.emptyList());
//        }
//        // 4.1.截取 from ~ end的部分
//        List<Long> ids = new ArrayList<>(list.size());
//        Map<String, Distance> distanceMap = new HashMap<>(list.size());
//        //获取end 跳过from  手动截取分页
//        list.stream().skip(from).forEach(result -> {
//            // 4.2.获取店铺id
//            String shopIdStr = result.getContent().getName();
//            ids.add(Long.valueOf(shopIdStr));
//            // 4.3.获取距离
//            Distance distance = result.getDistance();
//            distanceMap.put(shopIdStr, distance);
//        });
//        // 5.根据id查询Shop
//        String idStr = StrUtil.join(",", ids);
//        List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
//        for (Shop shop : shops) {
//            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
//        }
//        // 6.返回
//        return Result.ok(shops);
//    }
}
