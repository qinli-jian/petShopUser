package com.example.petshopuser.entity.DTO;


import lombok.Data;

import java.math.BigDecimal;


@Data
public class OrderOneDTO {
    private String user_id;
    private String commodity_id;
    private String order_address_id;
    private String specification_price_id;
    private String shop_cart_id;
    private Integer num;
}
