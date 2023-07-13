package com.example.petshopuser.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Ip_address {
    private String id;
    private String user_id;
    private String ip;
    private String ip_address;
    private Date create_time;
}
