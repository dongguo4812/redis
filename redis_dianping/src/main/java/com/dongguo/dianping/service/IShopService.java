package com.dongguo.dianping.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dongguo.dianping.entity.POJO.Shop;
import com.dongguo.dianping.entity.Result;

/**
 * <p>
 * 服务类
 * </p>
 *
 */
public interface IShopService extends IService<Shop> {

    Result queryShopById(Long id);

    Result updateShop(Shop shop);

//    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
