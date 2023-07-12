package com.example.petshopuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.Specification;
import com.example.petshopuser.entity.Specification_price;
import org.apache.ibatis.annotations.Mapper;
import com.example.petshopuser.controller.dto.CommodityCategoryDTO;
import com.example.petshopuser.controller.dto.CommodityIntroDTO;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CommodityMapper extends BaseMapper<Commodity> {
    Commodity getCommodityById(String id);

    List<Specification_price> getAllByCommodityId(String commodity_id);

    Specification getBySpecificationId(String id);
    List<CommodityIntroDTO> getAllCommodityIntro();

    List<CommodityIntroDTO> getCommodityIntroByKW(String kw);

    BigDecimal getCommodityMinPrice(String id);

    CommodityCategoryDTO getCategoryById(String categoryId);
}
