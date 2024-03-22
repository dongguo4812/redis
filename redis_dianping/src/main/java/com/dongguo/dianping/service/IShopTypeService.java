package com.dongguo.dianping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongguo.dianping.entity.POJO.ShopType;
import com.dongguo.dianping.entity.Result;

/**
 * <p>
 *  服务类
 * </p>
 */
public interface IShopTypeService extends IService<ShopType> {

    Result queryTypeList();

}
