package com.dongguo.redis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("tb_item_stock")
@Schema(title = "ItemStock", description = "商品库存表")
public class ItemStock {
    @TableId(type = IdType.INPUT, value = "item_id")
    @Schema(name = "id", description = "ID")
    private Long id; //商品id
    @Schema(name = "stock", description = "商品库存")
    private Integer stock; //商品库存
    @Schema(name = "sold", description = "商品销量")
    private Integer sold; //商品销量
}
