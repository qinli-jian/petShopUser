package com.example.petshopuser.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
@TableName(value = "user")
public class User {
    @TableField(value = "id")
    private String id;
    @TableField(value = "name")
    private String name;
    @TableField(value = "account")
    private String account;

    @JsonIgnore
    @TableField(value = "password")
    private String password;
    @TableField(value = "address")
    private String address;

}
