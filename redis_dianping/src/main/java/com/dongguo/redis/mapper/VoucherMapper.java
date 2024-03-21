package com.dongguo.redis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongguo.redis.entity.POJO.Voucher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 */
public interface VoucherMapper extends BaseMapper<Voucher> {

    List<Voucher> queryVoucherOfShop(@Param("shopId") Long shopId);
}
