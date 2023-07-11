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
    @TableField(value = "avatar")
    private String avatar;
    @TableField(value = "name")
    private String name;
    @TableField(value = "sex")
    private String sex;
    @TableField(value = "age")
    private String age;
    @TableField(value = "account")
    private String account;

    @TableField(value = "phone")
    private String phone;

    @JsonIgnore
    @TableField(value = "password")
    private String password;
    @TableField(value = "address")
    private String address;

    public User() {
    }

    public User(String id, String avatar, String name, String sex, String age, String account, String phone, String password, String address) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.account = account;
        this.phone = phone;
        this.password = password;
        this.address = address;
    }
}
