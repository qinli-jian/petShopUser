package com.example.petshopuser.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName(value = "specifications")
public class Specification {
    @TableField(value = "id")
    private String id;
    @TableField(value = "specification_name")
    private String specification_name;
    @TableField(value = "commodity_id")
    private String commodity_id;
    @TableField(value = "type")
    private String type;
    @TableField(value = "sales_volume")
    private Integer sales_volume;
    @TableField(value = "images")
    private String images;
    @TableField(value = "create_time")
    private Date create_time;

}
