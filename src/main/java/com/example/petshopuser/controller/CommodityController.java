package com.example.petshopuser.controller;

import com.example.petshopuser.common.Constants;
import com.example.petshopuser.controller.dto.CommodityCategoryDTO;
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
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/commodity")
public class CommodityController {

    @Resource
    private CommodityServiceImpl commodityService;

    @GetMapping("/search")
    public ReturnObj search(@RequestParam(value = "kw", required = false) String kw,@RequestParam(value = "category_id", required = false)String category_id,
                            @RequestParam(value = "pageNum")Integer pageNum,@RequestParam(value = "pageSize")Integer pageSize){
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

}
