package com.example.petshopuser.service.impl;

import com.example.petshopuser.entity.*;
import com.example.petshopuser.entity.DTO.After_sale_DTO;
import com.example.petshopuser.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.crypto.interfaces.PBEKey;
import java.util.List;

@Service
public class OrderServiceImpl {

    @Resource
    private OrderMapper orderMapper;

    public List<Order> getOrderById(String order_id){
        return orderMapper.getOrderById(order_id);
    }

    public List<Order_Status> getAllStatusById(String order_id){
        return orderMapper.getAllStatusById(order_id);
    }

    public boolean putOrder(Order order){return orderMapper.putOrder(order);}

    public boolean putOrderStatus(Order_Status order_status){return orderMapper.putOrderStatus(order_status);}

    public Status_description findStatusById(String id){return orderMapper.findStatusById(id);}
    public boolean putOCSList(List<Order_commodity_specification> OCSList ){
        for (Order_commodity_specification order_commodity_specification : OCSList) {
            if (!orderMapper.putOCS(order_commodity_specification)) {
                return false;
            }
        }
        return true;
    }
    public List<Order_commodity_specification> findOCSByOrder_Id(String order_id){
        return orderMapper.findOCSByOrderId(order_id);
    }


    public List<Order> getOrderByUserId(String user_id){
        return orderMapper.getOrderByUserId(user_id);
    }

    public List<String> getOrderIdsByUId(String user_id){ return orderMapper.getOrderIdsByUId(user_id);}

    public String findStatusId(String status_description){return orderMapper.findStatusId(status_description);}

    public Boolean deleteOrderById(String order_id){return (orderMapper.deleteOrderById(order_id)&&
            orderMapper.deleteOrderStatusByOrderId(order_id));}

    public Address getAddressById(String id){return orderMapper.getAddressById(id);}

    public Boolean setAfterSale(After_sale after_sale){return orderMapper.setAfterSale(after_sale);}

    public String getServiceTypeById(String id){return orderMapper.getServiceTypeById(id);}
}
