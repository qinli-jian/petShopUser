package com.example.petshopuser.controller;

import com.example.petshopuser.common.Constants;
import com.example.petshopuser.entity.DTO.ShopCarDTO;
import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.mapper.ShopCartMapper;
import com.example.petshopuser.service.impl.ShopCartServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/shopcart")
public class ShopCartController {

    @Resource
    private ShopCartServiceImpl shopCartService;

    // 查询购物车所有的列表，返回商品简介DTO，商品规格DTO，
    @GetMapping("/list")
    public ReturnObj getShopCarList(@RequestParam(value = "user_id") String user_id){
        ReturnObj returnObj = new ReturnObj();
        try{
            List<ShopCarDTO> shopCarDTOList = shopCartService.getList(user_id);
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
            returnObj.setData(shopCarDTOList);
        }catch (Exception e){
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }
        return returnObj;
    }

}
