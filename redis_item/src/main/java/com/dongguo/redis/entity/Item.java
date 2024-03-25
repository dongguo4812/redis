package com.dongguo.redis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;

@Data
@TableName("tb_item")
@Schema(title = "Item", description = "商品表")
public class Item {
    @TableId(type = IdType.AUTO)
    @Id
    @Schema(name = "id", description = "ID")
    private Long id;//商品id
    @Schema(name = "name", description = "商品名称")
    private String name;//商品名称
    @Schema(name = "title", description = "商品标题")
    private String title;//商品标题
    @Schema(name = "price", description = "价格")
    private Long price;//价格（分）
    @Schema(name = "image", description = "商品图片")
    private String image;//商品图片
    @Schema(name = "category", description = "分类名称")
    private String category;//分类名称
    @Schema(name = "brand", description = "品牌名称")
    private String brand;//品牌名称
    @Schema(name = "spec", description = "规格")
    private String spec;//规格
    @Schema(name = "status", description = "商品状态")
    private Integer status;//商品状态 1-正常，2-下架
    @Schema(name = "createTime", description = "创建时间")
    private Date createTime;//创建时间
    @Schema(name = "updateTime", description = "更新时间")
    private Date updateTime;//更新时间

    @TableField(exist = false)
    @Transient
    private Integer stock;
    @TableField(exist = false)
    @Transient
    private Integer sold;
}
