package com.example.petshopuser.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.HashMap;

@Data
@TableName("commodity")
public class Address {
    @TableField(value = "id")
    private String id;
    @TableField(value = "addressee")
    private String addressee;
    @TableField(value = "province")
    private String province;
    @TableField(value = "city")
    private String city;
    @TableField(value = "county")
    private String county;
    @TableField(value = "detailed_address")
    private String detailed_address;
    @TableField(value = "postcode")
    private String postcode;
    @TableField(value = "phone")
    private String phone;
    @TableField(value = "defaultAddress")
    private String defaultAddress;

    public Address() {
    }
    public Address(HashMap<String, String> address_obj) {
        this.id = address_obj.get("id");
        this.addressee = address_obj.get("addressee");
        this.province = address_obj.get("province");
        this.city = address_obj.get("city");
        this.county = address_obj.get(county);
        this.detailed_address = address_obj.get("detailed_address");
        this.postcode = address_obj.get("postcode");
        this.phone = address_obj.get("phone");
        this.defaultAddress = address_obj.get("defaultAddress");
    }

    public Address(String id, String addressee, String province, String city, String county, String detailed_address, String postcode, String phone, String defaultAddress) {
        this.id = id;
        this.addressee = addressee;
        this.province = province;
        this.city = city;
        this.county = county;
        this.detailed_address = detailed_address;
        this.postcode = postcode;
        this.phone = phone;
        this.defaultAddress = defaultAddress;
    }
}
