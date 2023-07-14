package com.example.petshopuser.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Order_Status {
    private String status_id;
    private String order_id;
    private String status_description;
    private Date create_time;
    public Order_Status(){

    }
    public Order_Status(Order_Status order_status){
        this.status_id = order_status.getStatus_id();
        this.order_id = order_status.getOrder_id();
        this.status_description = order_status.getStatus_description();
        this.create_time = order_status.getCreate_time();
    }
}
