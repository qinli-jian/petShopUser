package com.example.petshopuser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopuser.controller.dto.CommodityCategoryDTO;
import com.example.petshopuser.controller.dto.CommodityIntroDTO;
import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.mapper.CommodityMapper;
import com.example.petshopuser.service.ICommodityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements ICommodityService {

    @Resource
    private CommodityMapper commodityMapper;

    public List<CommodityIntroDTO> getAllCommodityIntro() {
        List<CommodityIntroDTO> commodityIntroDTOList = commodityMapper.getAllCommodityIntro();
        // 查询每个商品的最低规格组合价格
        for (CommodityIntroDTO commodityIntro : commodityIntroDTOList) {
            BigDecimal minPrice = commodityMapper.getCommodityMinPrice(commodityIntro.getId());
            commodityIntro.setPrice(minPrice);
            // 查询所属类别，包括一级类别 二级类比
            List<String> categoryList = new ArrayList<>();
            String categoryId = commodityIntro.getCategoryId();
            CommodityCategoryDTO commodityCategoryDTO = commodityMapper.getCategoryById(categoryId);
            categoryId = commodityCategoryDTO.getP_level_id();
            System.out.println(commodityCategoryDTO);
            categoryList.add(commodityCategoryDTO.getCategory_name());
            CommodityCategoryDTO p_commodityCategoryDTO = commodityMapper.getCategoryById(categoryId);
            categoryList.add(p_commodityCategoryDTO.getCategory_name());
            commodityIntro.setCategory(categoryList);
        }
        return commodityIntroDTOList;
    }

    public List<CommodityIntroDTO> getCommodityIntroByKW(String kw) {

        List<CommodityIntroDTO> commodityIntroDTOList = commodityMapper.getCommodityIntroByKW(kw);

        return commodityIntroDTOList;
    }
}
