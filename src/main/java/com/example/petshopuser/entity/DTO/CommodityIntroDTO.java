package com.example.petshopuser.entity.DTO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CommodityIntroDTO {
    private String id;
    private String name;
    private BigDecimal price;// 需要查询规格中最小的价格
    private String imgs;
    private String categoryId;
    private List<String> category;// 需要查询所属的类别
    private String createtime;

}
