package com.dongguo.redis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongguo.redis.entity.POJO.ShopType;
import com.dongguo.redis.entity.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IShopTypeService extends IService<ShopType> {

    Result queryTypeList();

}
