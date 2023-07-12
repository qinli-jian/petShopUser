package com.example.petshopuser.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "slideShow")
public class SlideShow {
    @TableField(value = "id")
    private String id;
    @TableField(value = "name")
    private String name;
    @TableField(value = "description")
    private String description;
    @TableField(value = "image")
    private String image;
    @TableField(value = "url")
    private String url;
}
