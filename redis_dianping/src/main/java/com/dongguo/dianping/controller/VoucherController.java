package com.dongguo.dianping.controller;


import com.dongguo.dianping.entity.POJO.Voucher;
import com.dongguo.dianping.entity.Result;
import com.dongguo.dianping.service.IVoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 *  前端控制器
 * </p>
 */
@RestController
@RequestMapping("/voucher")
@Tag(
        name = "VoucherController",
        description = "消费券接口")
public class VoucherController {

    @Resource
    private IVoucherService voucherService;

    /**
     * 新增普通券
     * @param voucher 优惠券信息
     * @return 优惠券id
     */
    @Operation(
            summary = "addVoucher",
            description = "新增普通券"
    )
    @PostMapping
    public Result addVoucher(@RequestBody Voucher voucher) {
        voucherService.save(voucher);
        return Result.ok(voucher.getId());
    }

    /**
     * 新增秒杀券
     * @param voucher 优惠券信息，包含秒杀信息
     * @return 优惠券id
     */
    @Operation(
            summary = "addSeckill",
            description = "新增秒杀券"
    )
    @PostMapping("/seckill")
    public Result addSeckillVoucher(@RequestBody Voucher voucher) {
        voucherService.addSeckillVoucher(voucher);
        return Result.ok(voucher.getId());
    }

    /**
     * 查询店铺的优惠券列表
     * @param shopId 店铺id
     * @return 优惠券列表
     */
    @Operation(
            summary = "list",
            description = "查询店铺的优惠券列表"
    )
    @GetMapping("/list/{shopId}")
    public Result queryVoucherOfShop(@PathVariable("shopId") Long shopId) {
       return voucherService.queryVoucherOfShop(shopId);
    }
    @Operation(
            summary = "seckill",
            description = "优惠券秒杀"
    )
    @GetMapping("/seckill/{voucherId}")
    public Result seckillVoucher(@PathVariable("voucherId") Long voucherId) {
        return voucherService.seckillVoucher(voucherId);
    }
}
