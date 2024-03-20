package com.dongguo.redis.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongguo.redis.entity.POJO.ShopType;
import com.dongguo.redis.entity.Result;
import com.dongguo.redis.mapper.ShopTypeMapper;
import com.dongguo.redis.service.IShopTypeService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.dongguo.redis.utils.RedisConstants.CACHE_SHOP_TYPE_KEY;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Result queryTypeList() {
        List<ShopType> shopTypeList;
        //查缓存
        List list = redisTemplate.opsForList().range(CACHE_SHOP_TYPE_KEY, 0, -1);
        if (CollUtil.isNotEmpty(list)) {
            return Result.ok(list);
        }
        //查不到查数据库
        shopTypeList = query().orderByAsc("sort").list();
        if (ObjectUtils.isEmpty(shopTypeList)) {
            return Result.fail("商铺类别查不到");
        }
        //保存到缓存
        redisTemplate.opsForList().leftPushAll(CACHE_SHOP_TYPE_KEY, shopTypeList);
        return Result.ok(shopTypeList);
    }
}
