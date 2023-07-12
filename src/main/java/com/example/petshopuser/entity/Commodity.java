package com.example.petshopuser.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("commodity")
public class Commodity {

    @TableField(value = "id")
    private String Id;

    @TableField(value = "name")
    private String Name;

    @TableField(value = "price")
    private String Price;

    @TableField(value = "imgs")
    private String Imgs;

    @TableField(value = "inventory")
    private Integer Inventory;

    @TableField(value = "category_id")
    private String CategoryId;

    @TableField(value = "description")
    private String Description;

    @TableField(value = "createTime", fill = FieldFill.INSERT_UPDATE)
    private String Createtime;

    @TableField(value = "specifications")
    private String Specifications;

    // getters and setters
}
