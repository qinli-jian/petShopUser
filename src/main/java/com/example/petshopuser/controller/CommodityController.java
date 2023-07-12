package com.example.petshopuser.controller;

import com.example.petshopuser.controller.dto.CommodityIntroDTO;
import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.service.impl.CommodityServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/commodity")
public class CommodityController {

    @Resource
    private CommodityServiceImpl commodityService;

    @GetMapping("/search")
    public ReturnObj search(@RequestParam(value = "kw") String kw,@RequestParam(value = "category_id")String category_id){
        ReturnObj returnObj = new ReturnObj();
        List<CommodityIntroDTO> commodityList = null;
        if(kw.isEmpty() && category_id.isEmpty()){
            // 搜索全部的，返回商品的简介对象
            commodityList = commodityService.getAllCommodityIntro();
        } else if (!kw.isEmpty() && category_id.isEmpty()) {
            // 根据kw关键字模糊
            commodityList = commodityService.getCommodityIntroByKW(kw);
        }
        returnObj.setData(commodityList);
        return returnObj;
    }

}
