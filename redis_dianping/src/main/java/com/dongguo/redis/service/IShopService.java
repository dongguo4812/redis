package com.dongguo.redis.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dongguo.redis.entity.POJO.Shop;
import com.dongguo.redis.entity.Result;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IShopService extends IService<Shop> {

    Result queryShopById(Long id);
//
//    Result updateShop(Shop shop);
//
//    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
