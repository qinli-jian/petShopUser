package com.example.petshopuser.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName(value = "order_info")
public class Order {
    @TableField(value = "order_id")
    private String order_id;
    @TableField(value = "commodity_id")
    private String commodity_id;
    @TableField(value = "user_id")
    private String user_id;
    @TableField(value = "specification")
    private String specification;
    @TableField(value = "num")
    private Integer num;
    @TableField(value = "total_price")
    private BigDecimal total_price;
    @TableField(value = "create_time")
    private Date create_time;
}
