package com.example.petshopuser.controller;

import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.entity.Specification;
import com.example.petshopuser.entity.Specification_price;
import com.example.petshopuser.service.impl.CommodityServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.petshopuser.controller.dto.CommodityIntroDTO;
import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.service.impl.CommodityServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/commodity")
public class CommodityController {
    @Resource
    private CommodityServiceImpl commodityService;
    @PostMapping("/details")
    public ReturnObj getCommodityById(@RequestBody Map<String,String> request_form){
        ReturnObj returnObj = new ReturnObj();
        Map<String,Object> data = new HashMap<>();
        Commodity commodity = commodityService.getCommodityById(request_form.get("id"));
        if(commodity!=null)
        data.put("commodity",commodity);
        else{
            returnObj.setCode("500");
            returnObj.setMsg("error");
            return returnObj;
        }
        List<Specification_price> specification_prices = commodityService.getAllByCommodityId(commodity.getId());
        System.out.println(specification_prices);
        List<Map<String,Object>> objects = new ArrayList<>();
        for (Specification_price specification_price : specification_prices) {
            System.out.println(specification_price.getSpecification_ids());
            if(specification_price.getSpecification_ids()!=null) {
                String[] temp_ids = specification_price.getSpecification_ids().split(",");
                List<Specification> specifications = new ArrayList<>();
                for (String temp_id : temp_ids) {
                        specifications.add(commodityService.getBySpecificationId(temp_id));
                }
                Map<String, Object> e = new HashMap<>();
                e.put("specifications", specifications);
                e.put("price", specification_price.getPrice());
                objects.add(e);
            }
        }
        data.put("specifications_price", objects);
        returnObj.setData( data);
        returnObj.setMsg("success");
        returnObj.setCode("200");

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
