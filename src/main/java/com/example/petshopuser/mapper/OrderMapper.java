package com.example.petshopuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopuser.entity.Order;
import com.example.petshopuser.entity.Order_Status;
import com.example.petshopuser.entity.Order_commodity_specification;
import com.example.petshopuser.entity.Status_description;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    Order getOrderById(String order_id);
    List<Order_Status> getAllStatusById(String order_id);

    boolean putOrder(Order order);

    boolean putOrderStatus(Order_Status order_status);

    Status_description findStatusById(String id);

    boolean putOCS(Order_commodity_specification OCS);

    List<Order_commodity_specification> findOCSByOrderId(String order_id);
}
