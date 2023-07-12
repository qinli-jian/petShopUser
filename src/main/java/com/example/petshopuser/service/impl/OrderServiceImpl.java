package com.example.petshopuser.service.impl;

import com.example.petshopuser.entity.Order;
import com.example.petshopuser.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderServiceImpl {

    @Resource
    private OrderMapper orderMapper;

    public Order getOrderById(String order_id){
        return orderMapper.getOrderById(order_id);
    }
}
