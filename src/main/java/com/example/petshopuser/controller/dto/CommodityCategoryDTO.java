package com.example.petshopuser.controller.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class CommodityCategoryDTO {
    private String id;
    @TableField(value = "category_name")
    private String category_name;
    private Timestamp createTime;
    private String level;
    @TableField(value = "p_level_id")
    private String p_level_id;

    private List<CommodityCategoryDTO> childen_category;

}
