package com.example.petshopuser.entity.DTO;

import com.example.petshopuser.entity.Commodity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CSDTO {
    private Commodity commodity;
    private String specification;
    private Integer num;
    private BigDecimal price;
}
