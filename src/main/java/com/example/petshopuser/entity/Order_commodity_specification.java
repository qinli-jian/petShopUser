package com.example.petshopuser.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order_commodity_specification {
    private String id;
    private String order_id;
    private String commodity_id;
    private String specifications;
    private Integer num;
    private BigDecimal price;
}
