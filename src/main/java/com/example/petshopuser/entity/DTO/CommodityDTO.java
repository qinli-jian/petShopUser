package com.example.petshopuser.entity.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CommodityDTO {

    private String commodity_id;
    private BigDecimal total_price;
    private String specification;
    private Integer num;


}
