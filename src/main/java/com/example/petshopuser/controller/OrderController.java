package com.example.petshopuser.controller;

import com.example.petshopuser.entity.*;
import com.example.petshopuser.service.impl.CommodityServiceImpl;
import com.example.petshopuser.service.impl.OrderServiceImpl;
import com.example.petshopuser.service.impl.UserServiceImpl;
import com.example.petshopuser.utils.SnowflakeIdWorker;

import com.example.petshopuser.utils.Utils;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static java.math.BigDecimal.ROUND_HALF_EVEN;

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

    // 创建订单
    @PostMapping("/create")
    public ReturnObj createOrder(@RequestBody Map<String,List<String>> request_form){
        ReturnObj returnObj = new ReturnObj();
        String order_id = String.valueOf(snowflakeIdWorker.nextId());
        String user_id = request_form.get("user_id").get(0);

        for(int i=0;i<request_form.get("commodity_id").size();i++){
            BigDecimal price =new BigDecimal(request_form.get("total_price").get(i));
            String commodity_id = request_form.get("commodity_id").get(i);
            String specification = request_form.get("specification").get(i);

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
            reTurnData(returnObj, order_id);
        }else{
            returnObj.setCode("500");
            returnObj.setMsg("error");
            returnObj.setData(false);
        }
        return returnObj;
    }

    // 支付订单
    @PostMapping("/pay_order")
    public ReturnObj payOrder(@RequestBody Map<String,String> request_form){
        ReturnObj returnObj = new ReturnObj();
        String order_id = request_form.get("order_id");
        if(order_id!=null){
            List<Order_Status> order_statusList = orderService.getAllStatusById(order_id);  //根据订单id获取订单状态列表
            if(!order_statusList.get(order_statusList.size()-1).getStatus_description()   //获取最新订单状态
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
            Order_Status order_status = null;


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
    public ReturnObj findOrdersBykey(@RequestParam Map<String,String> request_form){
        ReturnObj returnObj = new ReturnObj();
        String status = request_form.get("status");
        String user_id = request_form.get("user_id");
        String key     = request_form.get("key");
        List<String> order_id_list = orderService.getOrderIdsByUId(user_id);
        List<Map<String,Object>> order_list = new ArrayList<>();
        for(String order_id_item : order_id_list){
            Map<String,Object> order = new HashMap<>();
            List<Order_Status> order_statusList = orderService.getAllStatusById(order_id_item);
            Order_Status order_status = null;


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
        returnObj.setData(order_list);



        returnObj.setMsg("success");
        returnObj.setCode("200");
        return returnObj;
    }




}
