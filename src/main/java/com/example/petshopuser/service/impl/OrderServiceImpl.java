package com.example.petshopuser.service.impl;

import com.example.petshopuser.entity.Order;
import com.example.petshopuser.entity.Order_Status;
import com.example.petshopuser.entity.Status_description;
import com.example.petshopuser.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderServiceImpl {

    @Resource
    private OrderMapper orderMapper;

    public Order getOrderById(String order_id){
        return orderMapper.getOrderById(order_id);
    }

    public List<Order_Status> getAllStatusById(String order_id){
        return orderMapper.getAllStatusById(order_id);
    }

    public boolean putOrder(Order order){return orderMapper.putOrder(order);}

    public boolean putOrderStatus(Order_Status order_status){return orderMapper.putOrderStatus(order_status);}

    public Status_description findStatusById(String id){return orderMapper.findStatusById(id);}
}
