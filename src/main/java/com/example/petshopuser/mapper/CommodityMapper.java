package com.example.petshopuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopuser.controller.dto.CommodityCategoryDTO;
import com.example.petshopuser.controller.dto.CommodityIntroDTO;
import com.example.petshopuser.entity.Commodity;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CommodityMapper extends BaseMapper<Commodity> {
    List<CommodityIntroDTO> getAllCommodityIntro();

    List<CommodityIntroDTO> getCommodityIntroByKW(String kw);

    BigDecimal getCommodityMinPrice(String id);

    CommodityCategoryDTO getCategoryById(String categoryId);
}
