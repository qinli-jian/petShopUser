package com.example.petshopuser.entity.DTO;

import com.example.petshopuser.entity.Specification;
import com.example.petshopuser.entity.Specification_price;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Specification_priceDTO {

    private String id;
    private String commodity_id;
    // 商品的组合
    private List<Specification> specifications;
    // 价格从商品的组合表进行查询
    private BigDecimal price;
    private int inventory;
    private String image;

    public Specification_priceDTO() {
    }

    public Specification_priceDTO(Specification_price specification_price) {
        this.id = specification_price.getId();
        this.commodity_id = specification_price.getCommodity_id();
        this.price = specification_price.getPrice();
        this.inventory = specification_price.getInventory();
        this.image = specification_price.getImg();
    }
}
