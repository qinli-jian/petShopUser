package com.example.petshopuser.entity.DTO;

import com.example.petshopuser.entity.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDTO {
    private String user_id;
    private String order_address_id;
    private List<CommodityDTO> commodity_dto_list;
}
