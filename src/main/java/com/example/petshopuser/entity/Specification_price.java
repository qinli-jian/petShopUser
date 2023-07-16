package com.example.petshopuser.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;


@Data
@TableName(value = "specification_price")
public class Specification_price {
    @TableField(value = "id")
    private String id;
    @TableField(value = "commodity_id")
    private String commodity_id;
    @TableField(value = "specification_ids")
    private String specification_ids;
    @TableField(value = "price")
    private BigDecimal price;
    @TableField(value = "inventory")
    private int inventory;
    @TableField(value = "sales_volume")
    private int sales_volume;
    @TableField(value = "img")
    private String img;

}
