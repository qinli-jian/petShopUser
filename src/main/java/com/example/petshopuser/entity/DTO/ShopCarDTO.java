package com.example.petshopuser.entity.DTO;

import com.example.petshopuser.entity.Specification;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShopCarDTO {
    private String id;
    private String user_id;
    private CommodityIntroDTO commodityInfo;
    private Specification_priceDTO specification_price;
    private int amount;
    private List<Specification_priceDTO> all_specification_price;
    private Timestamp createTime;
}
