package com.example.petshopuser.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "commodity")
public class Commodity {
    @TableField(value = "id")
    private String id;
    @TableField(value = "name")
    private String name;
    @TableField(value = "imgs")
    private String imgs;
    @TableField(value = "category_id")
    private String category_id;
    @TableField(value = "description")
    private String description;
    @TableField(value = "createTime")
    private Date createTime;
    @TableField(value = "specifications")
    private String specifications;
    public Commodity() {
    }

    public Commodity(String id, String name, String imgs, String category_id, String description, Date createTime,
                     String specifications) {
        this.id = id;
        this.name = name;
        this.imgs = imgs;
        this.category_id = category_id;
        this.description = description;
        this.createTime = createTime;
        this.specifications = specifications;
    }
}
