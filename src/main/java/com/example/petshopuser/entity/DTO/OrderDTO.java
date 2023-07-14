package com.example.petshopuser.entity.DTO;

import com.example.petshopuser.entity.*;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private User user;
    private Order order;
    private List<CSDTO> CSDTOList;
    private List<Order_Status> order_statusList;

}
