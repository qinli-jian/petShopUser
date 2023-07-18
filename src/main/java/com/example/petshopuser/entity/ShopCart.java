package com.example.petshopuser.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
@TableName(value = "shopping_cart")
public class ShopCart {
    @Id
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

    public ShopCart() {
    }

    public ShopCart(String id, String user_id, String commodity_id, String specification_price_id, int amount) {
        this.id = id;
        this.user_id = user_id;
        this.commodity_id = commodity_id;
        this.specification_price_id = specification_price_id;
        this.amount = amount;
    }

}
