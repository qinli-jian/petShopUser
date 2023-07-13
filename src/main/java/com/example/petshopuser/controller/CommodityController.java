package com.example.petshopuser.controller;

import com.example.petshopuser.common.Constants;
import com.example.petshopuser.entity.*;
import com.example.petshopuser.entity.DTO.CommodityCategoryDTO;
import com.example.petshopuser.service.impl.CommodityServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.petshopuser.entity.DTO.CommodityIntroDTO;
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
    public ReturnObj getCommodityById(@RequestBody Map<String,String> request_form) {
        ReturnObj returnObj = new ReturnObj();
        Map<String, Object> data = new HashMap<>();
        Commodity commodity = commodityService.getCommodityById(request_form.get("id"));
        if (commodity != null){
            data.put("commodity", commodity);
            System.out.println(commodity.getCategory_id());
            Category category = commodityService.getCategoryById2(commodity.getCategory_id());
            if(category!=null){
                Category category1 = commodityService.getCategoryById2(category.getP_level_id());
                if(category1!=null){
                    data.put("categoryLevel"+category.getLevel(), category.getCategory_name());
                    data.put("categoryLevel"+category1.getLevel(), category1.getCategory_name());
                }else {
                    returnObj.setCode("500");
                    returnObj.setMsg("该分类不存在");
                    return returnObj;
                }
            }else{
                returnObj.setCode("500");
                returnObj.setMsg("该分类不存在");
                return returnObj;
            }
        }
        else {
            returnObj.setCode("500");
            returnObj.setMsg("error");
            return returnObj;
        }
        List<Specification_price> specification_prices = commodityService.getAllByCommodityId(commodity.getId());
        System.out.println(specification_prices);
        List<Map<String, Object>> objects = new ArrayList<>();
        for (Specification_price specification_price : specification_prices) {
            System.out.println(specification_price.getSpecification_ids());
            if (specification_price.getSpecification_ids() != null) {
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
        returnObj.setData(data);
        returnObj.setMsg("success");
        returnObj.setCode("200");
        return returnObj;
    }

    @GetMapping("/search")
    public ReturnObj search(@RequestParam(value = "kw", required = false) String kw,@RequestParam(value = "category_id", required = false)String category_id,@RequestParam(value = "pageNum")Integer pageNum,@RequestParam(value = "pageSize")Integer pageSize){
        ReturnObj returnObj = new ReturnObj();
        if(pageNum<=0 || pageSize <=0){
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("params error");
            return returnObj;
        }
        List<CommodityIntroDTO> commodityList = null;
        //
        if(kw==null && category_id==null || kw.isEmpty() && category_id.isEmpty()){
            // 搜索全部的，返回商品的简介对象
            commodityList = commodityService.getAllCommodityIntro(pageNum,pageSize);
        } else if (kw!=null && category_id==null || !kw.isEmpty() && category_id.isEmpty()) {
            // 根据kw关键字模糊
            commodityList = commodityService.getCommodityIntroByKW(kw,pageNum,pageSize);
        }else if (kw==null && category_id!=null || kw.isEmpty() && !category_id.isEmpty()) {
            // 根据类别的ID进行搜索，这个ID有可能是一级或者是二级的
            commodityList = commodityService.getCommodityIntroByCategoryId(category_id,pageNum,pageSize);
        }else{
            // 根据关键词和类别的ID进行搜索
            commodityList = commodityService.getCommodityIntroByCategoryId_Kw(kw,category_id,pageNum,pageSize);
        }
        returnObj.setCode(Constants.CODE_200);
        returnObj.setMsg("success");
        returnObj.setData(commodityList);
        return returnObj;
    }

    @GetMapping("/getAllCategory")
    public ReturnObj getAllCategory(){
        ReturnObj returnObj = new ReturnObj();
        try {
            List<CommodityCategoryDTO> categorys = commodityService.getAllCategory();
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
            returnObj.setData(categorys);
        }catch (Exception e){
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
            System.out.println("========getAllCategory");
            System.out.println(e);
        }
        return returnObj;
    }

    @GetMapping("/getAllSpecification")
    public ReturnObj getAllSpecification(){
        ReturnObj returnObj =new ReturnObj();
        try {
            List<Specification> specifications = commodityService.getAllSpecification();
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
            returnObj.setData(specifications);
        }catch (Exception e){
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
            System.out.println("========getAllSpecification");
            System.out.println(e);
        }
        return returnObj;
    }

}
