package com.example.petshopuser.controller;

import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.DTO.OrderDTO;
import com.example.petshopuser.entity.Order;
import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.entity.User;
import com.example.petshopuser.service.impl.CommodityServiceImpl;
import com.example.petshopuser.service.impl.OrderServiceImpl;
import com.example.petshopuser.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/user_order")
public class OrderController {
    @Resource
    private OrderServiceImpl orderService;
    @Resource
    private CommodityServiceImpl commodityService;
    @Resource
    private UserServiceImpl userService;
    @PostMapping("/details")
    public ReturnObj getOrderByCommodityIdAndUserId(@RequestBody Map<String,String> request_form){
        ReturnObj returnObj = new ReturnObj();
        Order order = orderService.getOrderById(request_form.get("order_id"));
        System.out.println(order);
        if(order!=null){
            System.out.println(order.getCommodity_id()+" "+order.getUser_id());
            Commodity commodity = commodityService.getCommodityById(order.getCommodity_id());
            System.out.println(commodity);
            User user = userService.getUserById(order.getUser_id());
            System.out.println(user);
            returnObj.setMsg("success");
            returnObj.setCode("200");
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrder(order);
            orderDTO.setCommodity(commodity);
            orderDTO.setUser(user);
            returnObj.setData(orderDTO);
        }
        else{
            returnObj.setCode("500");
            returnObj.setMsg("error");
        }
        return returnObj;
    }
}
