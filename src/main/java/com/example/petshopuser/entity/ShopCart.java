package com.example.petshopuser.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "shopping_cart")
public class ShopCart {
    @TableField(value = "id")
    private String id;
    @TableField(value = "user_id")
    private String user_id;
    @TableField(value = "commodity_id")
    private String commodity_id;
    @TableField(value = "specification_price_id")
    private String specification_price_id;
    @TableField(value = "amount")
    private int amount;
    @TableField(value = "createTime")
    private Timestamp createTime;
}
