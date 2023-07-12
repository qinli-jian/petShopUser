package com.example.petshopuser.entity.DTO;

import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.Order;
import com.example.petshopuser.entity.User;
import lombok.Data;

@Data
public class OrderDTO {
    private Commodity commodity;
    private User user;
    private Order order;

}
