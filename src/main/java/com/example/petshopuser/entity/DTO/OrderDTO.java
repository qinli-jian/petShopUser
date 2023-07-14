package com.example.petshopuser.entity.DTO;

import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.Order;
import com.example.petshopuser.entity.Order_Status;
import com.example.petshopuser.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private Commodity commodity;
    private User user;
    private Order order;
    private List<Order_Status> order_statusList;

}
