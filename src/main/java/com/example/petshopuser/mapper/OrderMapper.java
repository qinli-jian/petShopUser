package com.example.petshopuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopuser.entity.*;
import com.example.petshopuser.entity.DTO.After_sale_DTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    List<Order> getOrderById(String order_id);
    List<Order_Status> getAllStatusById(String order_id);

    boolean putOrder(Order order);

    boolean putOrderStatus(Order_Status order_status);

    String findStatusId(String status_description);

    Boolean deleteOrderById(String order_id);

    Boolean deleteOrderStatusByOrderId(String order_id);

    Status_description findStatusById(String id);

    boolean putOCS(Order_commodity_specification OCS);

    List<Order_commodity_specification> findOCSByOrderId(String order_id);

    List<Order> getOrderByUserId(String user_id);

    List<String> getOrderIdsByUId(String user_id);

    Address getAddressById(String id);

    Boolean setAfterSale(After_sale after_sale);

    String getServiceTypeById(String id);
}
