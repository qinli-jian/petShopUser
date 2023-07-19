package com.example.petshopuser.controller;

import com.example.petshopuser.entity.*;
import com.example.petshopuser.entity.DTO.After_sale_DTO;
import com.example.petshopuser.entity.DTO.OrderDTO;
import com.example.petshopuser.entity.DTO.OrderOneDTO;
import com.example.petshopuser.service.impl.CommodityServiceImpl;
import com.example.petshopuser.service.impl.OrderServiceImpl;
import com.example.petshopuser.utils.SnowflakeIdWorker;


import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


@RestController
@RequestMapping("/user_order")
public class OrderController {
    private final SnowflakeIdWorker snowflakeIdWorker;
    @Resource
    private OrderServiceImpl orderService;
    @Resource
    private CommodityServiceImpl commodityService;

    public OrderController(SnowflakeIdWorker snowflakeIdWorker) {
        this.snowflakeIdWorker = snowflakeIdWorker;
    }

    // 根据订单号获取订单详情
    @GetMapping("/details")
    public ReturnObj getOrderByCommodityIdAndUserId(@RequestParam String order_id){
        ReturnObj returnObj = new ReturnObj();

        Map<String,Object> order = new HashMap<>();
        List<Order_Status> order_statusList = orderService.getAllStatusById(order_id);
        if(!order_statusList.isEmpty()){
            order.put("order_status", order_statusList);
            Order_Status latest_status =  order_statusList.get(0);
            for(Order_Status item : order_statusList){
                if(orderService.findStatusId(item.getStatus_description())==null||
                        orderService.findStatusId(latest_status.getStatus_description())==null){
                    returnObj.setCode("500");
                    returnObj.setMsg(order_id+"该订单状态有错");
                    returnObj.setData(false);
                    return returnObj;
                }
                if(Integer.parseInt(orderService.findStatusId(item.getStatus_description()))
                        >Integer.parseInt(orderService.findStatusId(latest_status.getStatus_description()))){
                    latest_status = item;
                }

            }
            order.put("latest_status",latest_status);
        }
        else{
            returnObj.setCode("500");
            returnObj.setMsg(order_id+"该订单状态有错");
            returnObj.setData(false);
            return returnObj;
        }

        List<Order> order_list = orderService.getOrderById(order_id);
        order.put("order_id", order_list.get(0).getOrder_id());
        order.put("create_time", order_list.get(0).getCreate_time());
        order.put("address",order_list.get(0).getOrder_address());
        order.put("waybill", order_list.get(0).getWaybill());
        order.put("logistics_company", order_list.get(0).getLogistics_company());
        System.out.println(order_list);
        System.out.println(order_list.get(0).getAddress_id());
        order.put("address_msg", orderService.getAddressById(order_list.get(0).getAddress_id()));
        List<Map<String,Object>> commodity_list = new ArrayList<>();
        BigDecimal total_price =new BigDecimal(0);
        for(Order user_order_item : order_list){
            Map<String,Object> commodity = new HashMap<>();
            if(user_order_item.getOrder_id().equals(order_id)){
                String commodity_id =user_order_item.getCommodity_id();
                Commodity commodityO = commodityService.getCommodityById(commodity_id);
                commodity.put("commodity_id", commodityO.getId());
                commodity.put("commodity_name", commodityO.getName());
                commodity.put("image", commodityO.getImgs().split(", ")[0]);
                String[] specifications_C = user_order_item.getSpecification().split("\\+"); // 分割规格名
                List<Map<String,String>> specifications = new ArrayList<>();
                List<Specification> specificationList = commodityService.getAllSpecification(commodity_id);
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
                commodity.put("specifications", user_order_item.getSpecification());
                commodity.put("specification", specifications);
                commodity.put("num", user_order_item.getNum());
                commodity.put("price", user_order_item.getTotal_price().divide(new BigDecimal(user_order_item.getNum()),
                        RoundingMode.HALF_EVEN));
                total_price = total_price.add( user_order_item.getTotal_price());
                commodity_list.add(commodity);
            }
            order.put("commodity_list", commodity_list);
            order.put("commodity_num", commodity_list.size());
            order.put("total_price",total_price);
        }
        returnObj.setCode("200");
        returnObj.setData(order);
        returnObj.setMsg("success");
        return returnObj;
    }

    // 创建订单（一个订单多个商品）
    @PostMapping("/create")
    public ReturnObj createOrder(@RequestBody OrderDTO orderDTO){
        ReturnObj returnObj = new ReturnObj();
        String order_id = String.valueOf(snowflakeIdWorker.nextId());
        String user_id =  orderDTO.getUser_id();

        for(int i=0;i<orderDTO.getCommodity_dto_list().size();i++){
            BigDecimal price =orderDTO.getCommodity_dto_list().get(i).getTotal_price();
            String commodity_id = orderDTO.getCommodity_dto_list().get(i).getCommodity_id();
            String specification = orderDTO.getCommodity_dto_list().get(i).getSpecification();

            String[] specifications_C =specification.split("\\+"); //分割规格名
            List<Specification> specificationList = commodityService.getAllSpecification(commodity_id);
            int sign =0;
            for(String items : specifications_C){    //判断规格是否存在
                for(Specification item : specificationList){
                    if(item.getSpecification_name().equals(items)){
                        sign=1;
                        break;
                    }
                }
                if(sign==0){
                    returnObj.setCode("500");
                    returnObj.setMsg(items+"规格不存在");
                    returnObj.setData(false);
                }
                sign=0;
            }

            Integer num =  orderDTO.getCommodity_dto_list().get(i).getNum();
            Order order = new Order();
            order.setOrder_id(order_id);
            order.setUser_id(user_id);
            order.setCommodity_id(commodity_id);
            order.setAddress_id(orderDTO.getOrder_address_id());
            Address address = orderService.getAddressById(orderDTO.getOrder_address_id());
            order.setOrder_address(address.getProvince()+address.getCity()+address.getCounty()+address.getDetailed_address());
            order.setSpecification(specification);
            order.setNum(num);
            order.setTotal_price(price);
            if(!orderService.putOrder(order)){
                returnObj.setCode("500");
                returnObj.setMsg("error");
                returnObj.setData(false);
                return returnObj;
            }
        }

        Order_Status order_status = new Order_Status();     //插入订单初始状态
        order_status.setStatus_id(String.valueOf(snowflakeIdWorker.nextId()));
        order_status.setOrder_id(order_id);
        order_status.setStatus_description(orderService.findStatusById("2").getStatus_description()); //状态->已下单
        Order_Status order_status1 = new Order_Status(order_status);
        order_status1.setStatus_id(String.valueOf(snowflakeIdWorker.nextId()));
        order_status1.setStatus_description(orderService.findStatusById("3").getStatus_description());//状态->待付款
        if(orderService.putOrderStatus(order_status)&&
                orderService.putOrderStatus(order_status1)){
            Map<String,Object> returnData = new HashMap<>();
            returnData.put("order", orderService.getOrderById(order_id));
            returnData.put("order_status", orderService.getAllStatusById(order_id));
            returnData.put("address_msg", orderService.getAddressById(orderDTO.getOrder_address_id()));
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

    // 创建订单（一个订单一商品）
    @PostMapping("/create_one")
    public ReturnObj createOrderOne(@RequestBody OrderOneDTO orderDTO){
        System.out.println(orderDTO);
        ReturnObj returnObj = new ReturnObj();
        String order_id = String.valueOf(snowflakeIdWorker.nextId());
        String user_id =  orderDTO.getUser_id();
        String commodity_id = orderDTO.getCommodity_id();
        Integer num =  orderDTO.getNum();
        String specification_price_id = orderDTO.getSpecification_price_id();
        Specification_price specification_price = commodityService.getSpecificationPriceById(specification_price_id);

        BigDecimal price = specification_price.getPrice().multiply(BigDecimal.valueOf(num));
        String[] temp_ids = specification_price.getSpecification_ids().split(", "); //分割规格id
        String specification = "";
        int sign1=0;
        for (String temp_id : temp_ids) {
            sign1++;
            System.out.println(temp_id);
            Specification specification_item = commodityService.getBySpecificationId(temp_id);
            System.out.println(specification_item);
            specification=specification+specification_item.getSpecification_name();
            if(sign1%2!=0)
                specification+="+";
        }


        String[] specifications_C =specification.split("\\+"); //分割规格名
        List<Specification> specificationList = commodityService.getAllSpecification(commodity_id);
        int sign =0;
        for(String items : specifications_C){    //判断规格是否存在
            for(Specification item : specificationList){
                if(item.getSpecification_name().equals(items)){
                    sign=1;
                    break;
                }
            }
            if(sign==0){
                returnObj.setCode("500");
                returnObj.setMsg(items+"规格不存在");
                returnObj.setData(false);
            }
            sign=0;
        }
        Order order = new Order();
        order.setOrder_id(order_id);
        order.setUser_id(user_id);
        order.setCommodity_id(commodity_id);
        order.setAddress_id(orderDTO.getOrder_address_id());
        Address address = orderService.getAddressById(orderDTO.getOrder_address_id());
        order.setOrder_address(address.getProvince()+address.getCity()+address.getCounty()+address.getDetailed_address());
        order.setSpecification(specification);
        order.setNum(num);
        order.setTotal_price(price);
        if(!orderService.putOrder(order)){
            returnObj.setCode("500");
            returnObj.setMsg("error");
            returnObj.setData(false);
            return returnObj;
        }

        Order_Status order_status = new Order_Status();     //插入订单初始状态
        order_status.setStatus_id(String.valueOf(snowflakeIdWorker.nextId()));
        order_status.setOrder_id(order_id);
        order_status.setStatus_description(orderService.findStatusById("2").getStatus_description()); //状态->已下单
        Order_Status order_status1 = new Order_Status(order_status);
        order_status1.setStatus_id(String.valueOf(snowflakeIdWorker.nextId()));
        order_status1.setStatus_description(orderService.findStatusById("3").getStatus_description());//状态->待付款
        if(orderService.putOrderStatus(order_status)&&
                orderService.putOrderStatus(order_status1)){
            Map<String,Object> returnData = new HashMap<>();
            returnData.put("order", orderService.getOrderById(order_id));
            returnData.put("order_status", orderService.getAllStatusById(order_id));
            returnData.put("address_msg", orderService.getAddressById(orderDTO.getOrder_address_id()));
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



    //批量添加订单
    @PostMapping("/create_all")
    public ReturnObj createOrderAll(@RequestBody List<OrderOneDTO> orderDTOList){
        ReturnObj returnObj = new ReturnObj();
        List<Map<String,Object>> ReturnDataList = new ArrayList<>();
        for(OrderOneDTO orderDTO : orderDTOList) {
            String order_id = String.valueOf(snowflakeIdWorker.nextId());
            String user_id =  orderDTO.getUser_id();
            String commodity_id = orderDTO.getCommodity_id();
            Integer num =  orderDTO.getNum();
            String specification_price_id = orderDTO.getSpecification_price_id();
            Specification_price specification_price = commodityService.getSpecificationPriceById(specification_price_id);

            BigDecimal price = specification_price.getPrice().multiply(BigDecimal.valueOf(num));
            String[] temp_ids = specification_price.getSpecification_ids().split(", "); //分割规格id
            String specification = "";
            int sign1=0;
            for (String temp_id : temp_ids) {
                sign1++;
                System.out.println(temp_id);
                Specification specification_item = commodityService.getBySpecificationId(temp_id);
                System.out.println(specification_item);
                specification=specification+specification_item.getSpecification_name();
                if(sign1%2!=0)
                    specification+="+";
            }


            String[] specifications_C =specification.split("\\+"); //分割规格名
            List<Specification> specificationList = commodityService.getAllSpecification(commodity_id);
            int sign =0;
            for(String items : specifications_C){    //判断规格是否存在
                for(Specification item : specificationList){
                    if(item.getSpecification_name().equals(items)){
                        sign=1;
                        break;
                    }
                }
                if(sign==0){
                    returnObj.setCode("500");
                    returnObj.setMsg(items+"规格不存在");
                    returnObj.setData(false);
                }
                sign=0;
            }
            Order order = new Order();
            order.setOrder_id(order_id);
            order.setUser_id(user_id);
            order.setCommodity_id(commodity_id);
            order.setAddress_id(orderDTO.getOrder_address_id());
            Address address = orderService.getAddressById(orderDTO.getOrder_address_id());
            order.setOrder_address(address.getProvince()+address.getCity()+address.getCounty()+address.getDetailed_address());
            order.setSpecification(specification);
            order.setNum(num);
            order.setTotal_price(price);
            if(!orderService.putOrder(order)){
                returnObj.setCode("500");
                returnObj.setMsg("error");
                returnObj.setData(false);
                return returnObj;
            }

            Order_Status order_status = new Order_Status();     //插入订单初始状态
            order_status.setStatus_id(String.valueOf(snowflakeIdWorker.nextId()));
            order_status.setOrder_id(order_id);
            order_status.setStatus_description(orderService.findStatusById("2").getStatus_description()); //状态->已下单
            Order_Status order_status1 = new Order_Status(order_status);
            order_status1.setStatus_id(String.valueOf(snowflakeIdWorker.nextId()));
            order_status1.setStatus_description(orderService.findStatusById("3").getStatus_description());//状态->待付款
            if(orderService.putOrderStatus(order_status)&&
                    orderService.putOrderStatus(order_status1)){
                Map<String,Object> returnData = new HashMap<>();
                returnData.put("order", orderService.getOrderById(order_id));
                returnData.put("order_status", orderService.getAllStatusById(order_id));
                returnData.put("address_msg", orderService.getAddressById(orderDTO.getOrder_address_id()));
                ReturnDataList.add(returnData);
                returnObj.setMsg("success");
                returnObj.setCode("200");
            }else{
                returnObj.setCode("500");
                returnObj.setMsg("error");
                returnObj.setData(false);
            }


        }
        returnObj.setData(ReturnDataList);
        returnObj.setMsg("success");
        returnObj.setCode("200");
        return returnObj;
    }

    // 支付订单
    @PostMapping("/pay_order")
    public ReturnObj payOrder(@RequestBody Map<String,String> request_form){
        ReturnObj returnObj = new ReturnObj();
        String order_id = request_form.get("order_id");
        if(order_id!=null){
            List<Order_Status> order_statusList = orderService.getAllStatusById(order_id);  //根据订单id获取订单状态列表
            Order_Status order_status1 = null;
            if(!order_statusList.isEmpty()){
                Order_Status latest_status =  order_statusList.get(0);
                for(Order_Status item : order_statusList){

                    if(Integer.parseInt(orderService.findStatusId(item.getStatus_description()))
                            >Integer.parseInt(orderService.findStatusId(latest_status.getStatus_description()))){
                        latest_status = item;
                    }
                }
                order_status1 = latest_status;
            }
            else{
                returnObj.setCode("500");
                returnObj.setMsg(order_id+"该订单状态有错");
                returnObj.setData(false);
            }


            assert order_status1 != null;
            if(!order_status1.getStatus_description()   //获取最新订单状态
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
                putOrderStatus(returnObj, order_id, order_status);
            }
        }
        else{
            returnObj.setCode("500");
            returnObj.setMsg("error");
            returnObj.setData(false);
        }
        return returnObj;
    }

    // 设置订单状态
    @PostMapping("/set_order_status")
    public ReturnObj set_order_status(@RequestBody Map<String,String> request_form){
        ReturnObj returnObj = new ReturnObj();
        String order_id = request_form.get("order_id");
        String id = request_form.get("status_description_id");
        if(Integer.parseInt(id)<1||Integer.parseInt(id)>12){   //判断传入的状态id是否合法
            returnObj.setCode("500");
            returnObj.setMsg("没有该订单状态");
            returnObj.setData(false);
            return returnObj;
        }
        Order_Status order_status = new Order_Status();
        order_status.setStatus_id(String.valueOf(snowflakeIdWorker.nextId()));  //雪花算法生成id
        List<Order_Status> order_statusList = orderService.getAllStatusById(order_id);
        switch (id){     //判断订单状态是否合法
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
        order_status.setOrder_id(order_id);  //设置订单状态
        order_status.setStatus_description(orderService.findStatusById(id).getStatus_description());
        putOrderStatus(returnObj, order_id, order_status);
        return returnObj;
    }

    private void putOrderStatus(ReturnObj returnObj, String order_id, Order_Status order_status) { //设置订单状态
        if(orderService.putOrderStatus(order_status)){
            reTurnData(returnObj, order_id);
        }else{
            returnObj.setCode("500");
            returnObj.setMsg("error");
            returnObj.setData(false);
        }
    }

    private void reTurnData(ReturnObj returnObj, String order_id) {   //设置返回信息
        Map<String,Object> returnData = new HashMap<>();
        returnData.put("order", orderService.getOrderById(order_id));
        returnData.put("order_status", orderService.getAllStatusById(order_id));
        returnObj.setData(returnData);
        returnObj.setMsg("success");
        returnObj.setCode("200");
    }



    // 根据用户ID获取订单列表
    @PostMapping("/get_order_by_user_id")
    public ReturnObj getOrderByUserId(@RequestBody Map<String,String> request_form){
        ReturnObj returnObj = new ReturnObj();
        String status = request_form.get("status");
        String user_id = request_form.get("user_id");
        List<String> order_id_list = orderService.getOrderIdsByUId(user_id);
        List<Map<String,Object>> order_list = new ArrayList<>();
        for(String order_id_item : order_id_list){
            Map<String,Object> order = new HashMap<>();
            List<Order_Status> order_statusList = orderService.getAllStatusById(order_id_item);
            Order_Status order_status;

            if(!order_statusList.isEmpty()){
                Order_Status latest_status =  order_statusList.get(0);
                for(Order_Status item : order_statusList){
                    System.out.println("订单");
                    System.out.println(item.getStatus_description());
                    System.out.println(latest_status.getStatus_description());
                    if(Integer.parseInt(orderService.findStatusId(item.getStatus_description()))
                            >Integer.parseInt(orderService.findStatusId(latest_status.getStatus_description()))){
                        latest_status = item;
                    }
                }
                order_status = latest_status;
            }
            else{
                returnObj.setCode("500");
                returnObj.setMsg(order_id_item+"该订单状态有错");
                returnObj.setData(false);
                continue;
            }


            if(status.equals("*")||
                    (order_status.getStatus_description().equals(status))){
                order.put("order_id", order_id_item);
                order.put("status", order_status);  //设置订单状态
                order.put("address_msg",
                        orderService.getAddressById(orderService.getOrderById(order_id_item).get(0).getAddress_id()));

                List<Order> user_order_list = orderService.getOrderById(order_id_item);
                order.put("create_time", user_order_list.get(0).getCreate_time());
                List<Map<String,Object>> commodity_list = new ArrayList<>();
                BigDecimal total_price =new BigDecimal(0);
                for(Order user_order_item : user_order_list){
                    Map<String,Object> commodity = new HashMap<>();
                    if(user_order_item.getOrder_id().equals(order_id_item)){
                        String commodity_id =user_order_item.getCommodity_id();
                            Commodity commodityO = commodityService.getCommodityById(commodity_id);
                            commodity.put("commodity_id", commodityO.getId());
                            commodity.put("commodity_name", commodityO.getName());
                            commodity.put("image", commodityO.getImgs().split(", ")[0]);
                            String[] specifications_C = user_order_item.getSpecification().split("\\+"); // 分割规格名
                            List<Map<String,String>> specifications = new ArrayList<>();
                            List<Specification> specificationList = commodityService.getAllSpecification(commodity_id);
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
                            commodity.put("specifications", user_order_item.getSpecification());
                            commodity.put("specification", specifications);
                            commodity.put("num", user_order_item.getNum());
                            commodity.put("price", user_order_item.getTotal_price().divide(new BigDecimal(user_order_item.getNum()),
                                    RoundingMode.HALF_EVEN));
                            total_price = total_price.add( user_order_item.getTotal_price());
                            commodity_list.add(commodity);

                    }
                }
                order.put("total_price",total_price);
                order.put("commodity_list", commodity_list);

            }
            else{
                returnObj.setCode("500");
                returnObj.setMsg("订单号为: "+order_id_item+"的订单状态有错");
                returnObj.setData(false);
                continue;
            }
            order_list.add(order);
        }
        returnObj.setData(order_list);
        returnObj.setMsg("success");
        returnObj.setCode("200");
        return returnObj;
    }
    //删除订单
    @DeleteMapping("/delete")
    public ReturnObj deleteOrderById(@RequestParam String order_id){
        ReturnObj returnObj =new ReturnObj();

        if(orderService.deleteOrderById(order_id)){
            returnObj.setCode("200");
            returnObj.setData(true);
            returnObj.setMsg("删除成功");
        }else {
            returnObj.setCode("500");
            returnObj.setData(false);
            returnObj.setMsg("删除失败");
        }

        return returnObj;
    }

    //模糊查询订单
    @PostMapping("/findOrdersByKey")
    public ReturnObj findOrdersBykey(@RequestBody Map<String,String> request_form){
        ReturnObj returnObj = new ReturnObj();
        System.out.println(request_form);
        String status = request_form.get("status");
        String user_id = request_form.get("user_id");
        String key     = request_form.get("key");
        Date date_begin = null;
        Date date_end   = null;
        int date_sign = 0;
        if(!Objects.equals(request_form.get("date_begin"), "*") && !Objects.equals(request_form.get("date_end"), "*")){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                date_sign=1;
                LocalDate date_begin1 = LocalDate.parse( request_form.get("date_begin"), formatter);
                LocalDate date_end1 = LocalDate.parse( request_form.get("date_end"), formatter);
                System.out.println(date_begin1+","+date_end1);
                date_begin = Date.from(date_begin1.atStartOfDay(ZoneId.systemDefault()).toInstant());
                date_end = Date.from(date_end1.atStartOfDay(ZoneId.systemDefault()).toInstant());
                System.out.println("Converted Date: " + date_begin);
                System.out.println("Converted Date: " + date_end);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format: " + request_form.get("date_begin"));
                System.out.println("Invalid date format: " + request_form.get("date_end"));
                e.printStackTrace();
            }
        }

        List<String> order_id_list = orderService.getOrderIdsByUId(user_id);
        List<Map<String,Object>> order_list = new ArrayList<>();
        for(String order_id_item : order_id_list){
            Map<String,Object> order = new HashMap<>();
            List<Order_Status> order_statusList = orderService.getAllStatusById(order_id_item);
            Order_Status order_status ;


            if(!order_statusList.isEmpty()){
                Order_Status latest_status =  order_statusList.get(0);
                for(Order_Status item : order_statusList){

                    if(Integer.parseInt(orderService.findStatusId(item.getStatus_description()))
                            >Integer.parseInt(orderService.findStatusId(latest_status.getStatus_description()))){
                        latest_status = item;
                    }
                }
                order_status = latest_status;
            }
            else{
                returnObj.setCode("500");
                returnObj.setMsg(order_id_item+"该订单状态有错");
                returnObj.setData(false);
                continue;
            }


            if(status.equals("*")||
                    (order_status.getStatus_description().equals(status))){
                order.put("order_id", order_id_item);
                order.put("status", order_status);  //设置订单状态

                List<Order> user_order_list = orderService.getOrderByUserId(request_form.get("user_id"));
                order.put("create_time", user_order_list.get(0).getCreate_time());
                List<Map<String,Object>> commodity_list = new ArrayList<>();
                BigDecimal total_price =new BigDecimal(0);
                for(Order user_order_item : user_order_list){
                    Map<String,Object> commodity = new HashMap<>();
                    if(user_order_item.getOrder_id().equals(order_id_item)){
                        String commodity_id =user_order_item.getCommodity_id();
                        Commodity commodityO = commodityService.getCommodityById(commodity_id);
                        commodity.put("commodity_id", commodityO.getId());
                        commodity.put("commodity_name", commodityO.getName());
                        commodity.put("image", commodityO.getImgs().split(", ")[0]);
                        String[] specifications_C = user_order_item.getSpecification().split("\\+"); // 分割规格名
                        List<Map<String,String>> specifications = new ArrayList<>();
                        List<Specification> specificationList = commodityService.getAllSpecification(commodity_id);
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
                        commodity.put("specifications", user_order_item.getSpecification());
                        commodity.put("specification", specifications);
                        commodity.put("num", user_order_item.getNum());
                        commodity.put("price", user_order_item.getTotal_price().divide(new BigDecimal(user_order_item.getNum()),
                                RoundingMode.HALF_EVEN));
                        total_price = total_price.add( user_order_item.getTotal_price());
                        commodity_list.add(commodity);

                    }
                }
                order.put("total_price",total_price);
                order.put("commodity_list", commodity_list);

            }
            else{
                returnObj.setCode("500");
                returnObj.setMsg("订单号为: "+order_id_item+"的订单状态有错");
                returnObj.setData(false);
                continue;
            }
            order_list.add(order);
        }
        List<Map<String,Object>> returnData = new ArrayList<>();
        for(Map<String,Object> order : order_list){
            System.out.println(date_begin);
            System.out.println(date_end);
            if(date_sign==1){
                System.out.println(date_begin.after((Date) order.get("create_time")));
                assert date_end != null;
                System.out.println(date_end.before((Date) order.get("create_time")));
                if(date_begin.after((Date) order.get("create_time"))||date_end.before((Date) order.get("create_time")))
                    continue;
            }
            List<Map<String,Object>> commodity_listF = (List<Map<String, Object>>) order.get("commodity_list");
            for (Map<String,Object> commodity : commodity_listF){
                String commodity_name  = (String) commodity.get("commodity_name");
                if(commodity_name.contains(key)){
                    returnData.add(order);
                    break;
                }
            }
        }
        returnObj.setData(returnData);
        returnObj.setMsg("success");
        returnObj.setCode("200");
        return returnObj;
    }

    @PostMapping("/after_sale")
    public ReturnObj after_sale(@RequestBody After_sale_DTO after_sale_dto){
        ReturnObj returnObj =new ReturnObj();
        String user_id = after_sale_dto.getUser_id();
        String order_id = after_sale_dto.getOrder_id();
        String after_sale_content = after_sale_dto.getAfter_sale_content();
        String  getService_type = after_sale_dto.getService_type();
        List<String> imgList = after_sale_dto.getImgs();
        StringBuilder imgs= new StringBuilder();
        int sign=0;
        for(String item : imgList){
            sign++;
            imgs.append(item);
            if(sign%2!=0)
                imgs.append(",");
        }

        return returnObj;
    }



}
