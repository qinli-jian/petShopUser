package com.example.petshopuser.controller;

import com.example.petshopuser.common.Constants;
import com.example.petshopuser.entity.DTO.ShopCarDTO;
import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.mapper.ShopCartMapper;
import com.example.petshopuser.service.impl.ShopCartServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shopcart")
public class ShopCartController {

    @Resource
    private ShopCartServiceImpl shopCartService;

    // 查询购物车所有的列表，返回商品简介DTO，商品规格DTO，
    @GetMapping("/list")
    public ReturnObj getShopCarList(@RequestParam(value = "user_id") String user_id){
        ReturnObj returnObj = new ReturnObj();
        List<ShopCarDTO> shopCarDTOList = shopCartService.getList(user_id);
        try{
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
            returnObj.setData(shopCarDTOList);
        }catch (Exception e){
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }
        return returnObj;
    }

    // 加入商品到购物车
    @PostMapping("/addtoshopcart")
    public ReturnObj addToShopCart(@RequestParam(value = "user_id") String user_id,@RequestBody Map<String,ArrayList<Map<String,String>>> addToShopCartInfo){
        ReturnObj returnObj = new ReturnObj();

//        String user_id = addToShopCartInfo.get("user_id");
//        String commodity_id = addToShopCartInfo.get("commodity_id");
//        String specification_price_id = addToShopCartInfo.get("specification_price_id");
        // commodity_id、specification_price_id和amount数据都在infoList里面，只需要循环去除就行
        ArrayList<Map<String, String>> infoList = addToShopCartInfo.get("infoList");
        int am = infoList.size();
        System.out.println("shopcart");
        System.out.println(user_id);
        System.out.println(infoList);
        int f = shopCartService.insert_shopCart(user_id,infoList);
        if(am!=f){
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("insert failed");
        }else{
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("insert success");
        }

        return returnObj;
    }

}
