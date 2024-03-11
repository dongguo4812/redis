package com.dongguo.redis.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author: Administrator
 * @Date: 2024-02-27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "商品表", description = "聚划算活动商品表")
public class Product {
    //产品id
    private Long id;
    //产品名称
    private String name;
    //产品价格
    private BigDecimal price;
    //产品描述
    private String detail;
}
