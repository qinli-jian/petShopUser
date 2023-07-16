package com.example.petshopuser.controller;

import com.example.petshopuser.entity.*;
import com.example.petshopuser.entity.DTO.CSDTO;
import com.example.petshopuser.entity.DTO.OrderDTO;
import com.example.petshopuser.service.impl.CommodityServiceImpl;
import com.example.petshopuser.service.impl.OrderServiceImpl;
import com.example.petshopuser.service.impl.UserServiceImpl;
import com.example.petshopuser.utils.SnowflakeIdWorker;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/user_order")
public class OrderController {
    private final SnowflakeIdWorker snowflakeIdWorker;
    @Resource
    private OrderServiceImpl orderService;
    @Resource
    private CommodityServiceImpl commodityService;
    @Resource
    private UserServiceImpl userService;

    public OrderController(SnowflakeIdWorker snowflakeIdWorker) {
        this.snowflakeIdWorker = snowflakeIdWorker;
    }


    @PostMapping("/details")
    public ReturnObj getOrderByCommodityIdAndUserId(@RequestBody Map<String,String> request_form){
        ReturnObj returnObj = new ReturnObj();
        String order_id = request_form.get("order_id");
        List<Order> orders = orderService.getOrderById(order_id);//获取订单数据
        System.out.println(orders);
        if(orders!=null){
//            OrderDTO orderDTO = new OrderDTO();
//            orderDTO.setOrder(order);
//            System.out.println(order.getCommodity_id()+" "+order.getUser_id());
//            List<Order_commodity_specification> OCSList = orderService.findOCSByOrder_Id(order_id);
//            List<CSDTO> CSDTOList = new ArrayList<>();
//            for(Order_commodity_specification OCS:OCSList){
//                CSDTO csDTO = new CSDTO();
//                Commodity commodity = commodityService.getCommodityById(OCS.getCommodity_id());
//                System.out.println(commodity);
//                csDTO.setCommodity(commodity);
//                csDTO.setSpecification(OCS.getSpecifications());
//                csDTO.setNum(OCS.getNum());
//                csDTO.setPrice(OCS.getPrice());
//                CSDTOList.add(csDTO);
//
//            }
//            orderDTO.setCSDTOList(CSDTOList);
//            User user = userService.getUserById(order.getUser_id());
//            System.out.println(user);
//            orderDTO.setUser(user);
//            returnObj.setMsg("success");
//            returnObj.setCode("200");
//            returnObj.setData(orderDTO);
            BigDecimal total_price =  new BigDecimal(0);
            for(Order order : orders){
                total_price=total_price.add(order.getTotal_price());
            }
            Map<String,Object> data = new HashMap<>();
            data.put("order_id", orders.get(0).getOrder_id());
            data.put("orders", orders);
            data.put("order_status", orderService.getAllStatusById(order_id));
            data.put("total_price", total_price);
            returnObj.setData(data);
            returnObj.setMsg("success");
            returnObj.setCode("200");
        }
        else{
            returnObj.setCode("500");
            returnObj.setMsg("error");
        }
        return returnObj;
    }
    @PostMapping("/create")
    public ReturnObj createOrder(@RequestBody Map<String,List<String>> request_form){
        ReturnObj returnObj = new ReturnObj();
        String order_id = String.valueOf(snowflakeIdWorker.nextId());
        String user_id = request_form.get("user_id").get(0);
//        List<Order_commodity_specification> OCSList = new ArrayList<>();
//        BigDecimal total_price = BigDecimal.valueOf(0);
//        Integer commodity_num=0;
        for(int i=0;i<request_form.get("commodity_id").size();i++){
            BigDecimal price =new BigDecimal(request_form.get("total_price").get(i));
//            total_price=total_price.add(price);
            String commodity_id = request_form.get("commodity_id").get(i);
            String specification = request_form.get("specification").get(i);
            Integer num =  Integer.parseInt(request_form.get("num").get(i));
            Order order = new Order();
            order.setOrder_id(order_id);
            order.setUser_id(user_id);
            order.setCommodity_id(commodity_id);
            order.setSpecification(specification);
            order.setNum(num);
            order.setTotal_price(price);
            if(!orderService.putOrder(order)){
                returnObj.setCode("500");
                returnObj.setMsg("error");
                returnObj.setData(false);
                return returnObj;
            }
//            Order_commodity_specification OCS =new Order_commodity_specification();
//            OCS.setOrder_id(order_id);
//            OCS.setCommodity_id(commodity_id);
//            OCS.setSpecifications(specification);
//            OCS.setNum(num);
//            OCS.setPrice(price);
//            String id = String.valueOf(snowflakeIdWorker.nextId());
//            OCS.setId(id);
//            OCSList.add(OCS);
//            commodity_num++;
        }
//        order.setTotal_price(total_price);
//        order.setNum(commodity_num);
        Order_Status order_status = new Order_Status();
        order_status.setStatus_id(String.valueOf(snowflakeIdWorker.nextId()));
        order_status.setOrder_id(order_id);
        order_status.setStatus_description(orderService.findStatusById("2").getStatus_description());
        Order_Status order_status1 = new Order_Status(order_status);
        order_status1.setStatus_id(String.valueOf(snowflakeIdWorker.nextId()));
        order_status1.setStatus_description(orderService.findStatusById("3").getStatus_description());
        if(orderService.putOrderStatus(order_status)&&
                orderService.putOrderStatus(order_status1)){
            Map<String,Object> returnData = new HashMap<>();
            returnData.put("order", orderService.getOrderById(order_id));
            returnData.put("order_status", orderService.getAllStatusById(order_id));
            returnObj.setData(returnData);
            returnObj.setMsg("success");
            returnObj.setCode("200");
        }else{
            returnObj.setCode("500");
            returnObj.setMsg("error");
            returnObj.setData(false);
        }
        return returnObj;
    }
    @PostMapping("/pay_order")
    public ReturnObj payOrder(@RequestBody Map<String,String> request_form){
        ReturnObj returnObj = new ReturnObj();
        String order_id = request_form.get("order_id");
        if(order_id!=null){
            List<Order_Status> order_statusList = orderService.getAllStatusById(order_id);
            if(!order_statusList.get(order_statusList.size()-1).getStatus_description()
                    .equals(orderService.findStatusById("3").getStatus_description())){
                returnObj.setCode("500");
                returnObj.setMsg("该订单未下单或已支付");
                returnObj.setData(false);
            }
            else{
                Order_Status order_status = new Order_Status();
                order_status.setStatus_id(String.valueOf(snowflakeIdWorker.nextId()));
                order_status.setOrder_id(order_id);
                order_status.setStatus_description(orderService.findStatusById("4").getStatus_description());
                if(orderService.putOrderStatus(order_status)){
                    Map<String,Object> returnData = new HashMap<>();
                    returnData.put("order", orderService.getOrderById(order_id));
                    returnData.put("order_status", orderService.getAllStatusById(order_id));
                    returnObj.setData(returnData);
                    returnObj.setMsg("success");
                    returnObj.setCode("200");
                }else{
                    returnObj.setCode("500");
                    returnObj.setMsg("error");
                    returnObj.setData(false);
                }
            }
        }
        else{
            returnObj.setCode("500");
            returnObj.setMsg("error");
            returnObj.setData(false);
        }
        return returnObj;
    }
    @PostMapping("/set_order_status")
    public ReturnObj set_order_status(@RequestBody Map<String,String> request_form){
        ReturnObj returnObj = new ReturnObj();
        String order_id = request_form.get("order_id");
        String id = request_form.get("status_description_id");
        if(Integer.parseInt(id)<1||Integer.parseInt(id)>12){
            returnObj.setCode("500");
            returnObj.setMsg("没有该订单状态");
            returnObj.setData(false);
            return returnObj;
        }
        Order_Status order_status = new Order_Status();
        order_status.setStatus_id(String.valueOf(snowflakeIdWorker.nextId()));
        List<Order_Status> order_statusList = orderService.getAllStatusById(order_id);
        switch (id){
            case "1":{
                if(order_statusList==null){
                    returnObj.setCode("500");
                    returnObj.setMsg("没有该订单");
                    returnObj.setData(false);
                    return returnObj;
                }
                break;
            }
            case "2":{
                if(order_statusList!=null){
                    returnObj.setCode("500");
                    returnObj.setMsg("该订单已存在");
                    returnObj.setData(false);
                    return returnObj;
                }
                break;
            }
            case "3":{
                if(!order_statusList.get(order_statusList.size()-1).getStatus_description()
                        .equals(orderService.findStatusById("2").getStatus_description())){
                    returnObj.setCode("500");
                    returnObj.setMsg("该订单"+order_statusList.get(order_statusList.size()-1).getStatus_description());
                    returnObj.setData(false);
                    return returnObj;
                }
                break;
            }
            case "4":{
                if(!order_statusList.get(order_statusList.size()-1).getStatus_description()
                        .equals(orderService.findStatusById("3").getStatus_description())){
                    returnObj.setCode("500");
                    returnObj.setMsg("该订单"+order_statusList.get(order_statusList.size()-1).getStatus_description());
                    returnObj.setData(false);
                    return returnObj;
                }
                break;
            }
            case "5":{
                if(!order_statusList.get(order_statusList.size()-1).getStatus_description()
                        .equals(orderService.findStatusById("4").getStatus_description())){
                    returnObj.setCode("500");
                    returnObj.setMsg("该订单"+order_statusList.get(order_statusList.size()-1).getStatus_description());
                    returnObj.setData(false);
                    return returnObj;
                }
                if(!order_statusList.get(order_statusList.size()-1).getStatus_description()
                        .equals(orderService.findStatusById("11").getStatus_description())){
                    returnObj.setCode("500");
                    returnObj.setMsg("该订单"+order_statusList.get(order_statusList.size()-1).getStatus_description());
                    returnObj.setData(false);
                    return returnObj;
                }
                break;
            }
            case "6":{
                if(!order_statusList.get(order_statusList.size()-1).getStatus_description()
                        .equals(orderService.findStatusById("5").getStatus_description())){

                    returnObj.setMsg("该订单"+order_statusList.get(order_statusList.size()-1).getStatus_description());
                    returnObj.setCode("500");
                    returnObj.setData(false);
                    return returnObj;
                }
                break;
            }
            case "7":{
                if(!order_statusList.get(order_statusList.size()-1).getStatus_description()
                        .equals(orderService.findStatusById("6").getStatus_description())){
                    returnObj.setCode("500");
                    returnObj.setMsg("该订单"+order_statusList.get(order_statusList.size()-1).getStatus_description());
                    returnObj.setData(false);
                    return returnObj;
                }
                break;
            }
            case "8":{
                if(!order_statusList.get(order_statusList.size()-1).getStatus_description()
                        .equals(orderService.findStatusById("7").getStatus_description())){
                    returnObj.setCode("500");
                    returnObj.setMsg("该订单"+order_statusList.get(order_statusList.size()-1).getStatus_description());
                    returnObj.setData(false);
                    return returnObj;
                }
                break;
            }
            case "9": {
                if(!order_statusList.get(order_statusList.size()-1).getStatus_description()
                        .equals(orderService.findStatusById("8").getStatus_description())){
                    returnObj.setCode("500");
                    returnObj.setMsg("该订单"+order_statusList.get(order_statusList.size()-1).getStatus_description());
                    returnObj.setData(false);
                    return returnObj;
                }
                break;
            }
        }
        order_status.setOrder_id(order_id);
        order_status.setStatus_description(orderService.findStatusById(id).getStatus_description());
        if(orderService.putOrderStatus(order_status)){
            Map<String,Object> returnData = new HashMap<>();
            returnData.put("order", orderService.getOrderById(order_id));
            returnData.put("order_status", orderService.getAllStatusById(order_id));
            returnObj.setData(returnData);
            returnObj.setMsg("success");
            returnObj.setCode("200");
        }else{
            returnObj.setCode("500");
            returnObj.setMsg("error");
            returnObj.setData(false);
        }
        return returnObj;
    }

    @PostMapping("/get_order_by_user_id")
    public ReturnObj getOrderByUserId(@RequestBody Map<String,String> request_form){
        ReturnObj returnObj = new ReturnObj();
        String status = request_form.get("status");
        List<Order> orderList = orderService.getOrderByUserId(request_form.get("user_id"));
        if(orderList!=null){
            Map<String,Object> data = new HashMap<>();
            List<Map<String,Object>> dataList = new ArrayList<>();
            for(Order order : orderList){
                List<Order_Status> order_statusList = orderService.getAllStatusById(order.getOrder_id());
                Order_Status order_status = null;
                if(!order_statusList.isEmpty())
                     order_status = order_statusList.get(order_statusList.size()-1);
                if(status.equals("*")||
                        (order_status!=null&&order_status.getStatus_description().equals(status))){
                    data.put("order_status", order_status);
                    data.put("order_create_time", order.getCreate_time());
                    data.put("order_id", order.getOrder_id());
                    data.put("total_price", order.getTotal_price());
                    data.put("num", order.getNum());
                    Commodity commodity = commodityService.getCommodityById(order.getCommodity_id());
                    data.put("commodity", commodity);
                    String[] specifications_C = order.getSpecification().split("\\+");
                    System.out.println(Arrays.toString(specifications_C));
                    List<Map<String,String>> specifications = new ArrayList<>();
                    List<Specification> specificationList = commodityService.getAllSpecification();
                    for(String item : specifications_C){
                        for(Specification specification : specificationList){
                            if(specification.getSpecification_name().equals(item)){
                                Map<String,String> temp = new HashMap<>();
                                temp.put("type",specification.getType());
                                temp.put("value", item);
                                specifications.add(temp);
                                break;
                            }
                        }
                    }
                    data.put("specifications",specifications);
                    dataList.add(data);
                }
            }
            returnObj.setCode("200");
            returnObj.setMsg("success");
            returnObj.setData(dataList);
        }
        else{
            returnObj.setCode("500");
            returnObj.setMsg("error");
            returnObj.setData(false);
        }
        return returnObj;
    }
}