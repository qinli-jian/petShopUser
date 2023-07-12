package com.example.petshopuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopuser.controller.dto.CommodityCategoryDTO;
import com.example.petshopuser.controller.dto.CommodityIntroDTO;
import com.example.petshopuser.entity.Commodity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface CommodityMapper extends BaseMapper<Commodity> {
    List<CommodityIntroDTO> getAllCommodityIntro(int offset,int pageSize);

    List<CommodityIntroDTO> getCommodityIntroByKW(String kw,int offset,int pageSize);

    BigDecimal getCommodityMinPrice(String id);

    CommodityCategoryDTO getCategoryById(String categoryId);

    List<CommodityCategoryDTO> getAll2Category();

    List<CommodityCategoryDTO> getAll1Category();

    List<CommodityCategoryDTO> getChildCategoryByPlevel(String p_level_id);

    List<CommodityIntroDTO> getCommodityIntroByCategoryId(String category_id,int offset,int pageSize);


    List<CommodityIntroDTO> getCommodityIntroByCategoryIdList(@Param("child_category_ids") ArrayList<String> child_category_ids,int offset,int pageSize);

    List<CommodityIntroDTO> getCommodityIntrosByCategoryId_Kw(String kw, String child_category_ids, int offset, int pageSize);
}
