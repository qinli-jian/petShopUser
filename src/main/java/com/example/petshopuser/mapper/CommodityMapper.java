package com.example.petshopuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopuser.entity.*;
import com.example.petshopuser.entity.Category;
import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.DTO.Specification_priceDTO;
import com.example.petshopuser.entity.Specification;
import com.example.petshopuser.entity.Specification_price;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.petshopuser.entity.DTO.CommodityCategoryDTO;
import com.example.petshopuser.entity.DTO.CommodityIntroDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface CommodityMapper extends BaseMapper<Commodity> {
    List<CommodityIntroDTO> getAllCommodityIntro(int offset,int pageSize,String ranking);
    Commodity getCommodityById(String id);

    List<Specification_price> getAllByCommodityId(String commodity_id);
    Specification_price getSpecificationPriceById(String id);

    Specification getBySpecificationId(String id);


    List<CommodityIntroDTO> getCommodityIntroByKW(String kw,int offset,int pageSize,String ranking);

    BigDecimal getCommodityMinPrice(String id);

    CommodityCategoryDTO getCategoryById(String categoryId);

    List<CommodityCategoryDTO> getAll2Category();

    List<CommodityCategoryDTO> getAll1Category();

    List<CommodityCategoryDTO> getChildCategoryByPlevel(String p_level_id);

    List<CommodityIntroDTO> getCommodityIntroByCategoryId(String category_id,int offset,int pageSize,String ranking);


    List<CommodityIntroDTO> getCommodityIntroByCategoryIdList(@Param("child_category_ids") ArrayList<String> child_category_ids,int offset,int pageSize,String ranking);

    Category getCategoryById2(String id);

    Boolean setComments(Comment comment);

    List<Comment> findCommentsByCommodity_Id(String commodity_id);

    Comment findCommentsById(String id);
    List<Specification> getAllSpecification(String comodity_id);
    List<CommodityIntroDTO> getCommodityIntrosByCategoryId_Kw(String kw, ArrayList<String> child_category_ids, int offset, int pageSize,String ranking);

    CommodityIntroDTO getCommodityIntroById(String commodity_id);

    Specification_price getSpecification_priceById(String specification_id);

    List<Specification_price> getSpecification_priceByCommodity_id(String commodity_id);
}
