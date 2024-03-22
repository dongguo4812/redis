package com.dongguo.dianping.controller;


import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.service.IVoucherOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/voucher-order")
@Tag(
        name = "VoucherOrderController",
        description = "消费券订单接口")
public class VoucherOrderController {
    @Autowired
    private IVoucherOrderService voucherOrderService;

    @GetMapping("/seckill/{voucherId}")
    public Result seckillVoucher(@PathVariable("voucherId") Long voucherId) {
        return voucherOrderService.seckillVoucher(voucherId);
    }
}
