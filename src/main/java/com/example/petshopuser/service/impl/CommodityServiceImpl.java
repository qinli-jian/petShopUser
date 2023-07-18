package com.example.petshopuser.service.impl;

import com.example.petshopuser.entity.*;
import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.DTO.Specification_priceDTO;
import com.example.petshopuser.entity.Specification;
import com.example.petshopuser.entity.Specification_price;
import com.example.petshopuser.mapper.CommodityMapper;
import org.springframework.stereotype.Service;
import com.example.petshopuser.entity.DTO.CommodityCategoryDTO;
import com.example.petshopuser.entity.DTO.CommodityIntroDTO;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CommodityServiceImpl {

    @Resource
    private CommodityMapper commodityMapper;

    public Commodity getCommodityById(String id){
        return commodityMapper.getCommodityById(id);
    }

    public List<Specification_price> getAllByCommodityId(String commodity_id){
        return commodityMapper.getAllByCommodityId(commodity_id);
    }
    public Specification getBySpecificationId(String id) {
        return commodityMapper.getBySpecificationId(id);
    }

    public List<CommodityIntroDTO> getAllCommodityIntro(int pageNum,int pageSize,String ranking) {
        int offset = pageSize*(pageNum-1);
        List<CommodityIntroDTO> commodityIntroDTOList = commodityMapper.getAllCommodityIntro(offset,pageSize,ranking);
        // 查询每个商品的最低规格组合价格
        for (CommodityIntroDTO commodityIntro : commodityIntroDTOList) {
            BigDecimal minPrice = commodityMapper.getCommodityMinPrice(commodityIntro.getId());
            commodityIntro.setPrice(minPrice);
            // 查询所属类别，包括一级类别 二级类比
            List<CommodityCategoryDTO> categoryList = new ArrayList<>();
            String categoryId = commodityIntro.getCategoryId();
            CommodityCategoryDTO commodityCategoryDTO = commodityMapper.getCategoryById(categoryId);
            categoryId = commodityCategoryDTO.getP_level_id();
            System.out.println(commodityCategoryDTO);
            categoryList.add(commodityCategoryDTO);
            CommodityCategoryDTO p_commodityCategoryDTO = commodityMapper.getCategoryById(categoryId);
            categoryList.add(p_commodityCategoryDTO);
            commodityIntro.setCategory(categoryList);
        }
        return commodityIntroDTOList;
    }

    public List<CommodityIntroDTO> getCommodityIntroByKW(String kw,int pageNum,int pageSize,String ranking) {
        int offset = pageSize*(pageNum-1);
        List<CommodityIntroDTO> commodityIntroDTOList = commodityMapper.getCommodityIntroByKW(kw,offset,pageSize,ranking);

        return commodityIntroDTOList;
    }

    public List<CommodityCategoryDTO> getAllCategory() {
        // 获取到了一级分类的菜单数据
        List<CommodityCategoryDTO> p_commodityCategoryDTOList = commodityMapper.getAll1Category();
//        List<CommodityCategoryDTO> chil_commodityCategoryDTOList = commodityMapper.getAll2Category();
        for (CommodityCategoryDTO p_category:
                p_commodityCategoryDTOList) {
            // 获取同一父级下的二级分类的列表
            String p_level_id = p_category.getId();
            List<CommodityCategoryDTO> chil_commodityCategoryDTOList = commodityMapper.getChildCategoryByPlevel(p_level_id);
            p_category.setChilden_category(chil_commodityCategoryDTOList);
        }
        return p_commodityCategoryDTOList;
    }

    public List<CommodityIntroDTO> getCommodityIntroByCategoryId(String category_id,int pageNum,int pageSize,String ranking) {
        int offset = pageSize*(pageNum-1);
        List<CommodityIntroDTO> commodityIntroDTOList = null;
        //首先在商品表中通过对比级别id进行查询
        commodityIntroDTOList = commodityMapper.getCommodityIntroByCategoryId(category_id,offset,pageSize,"");
        System.out.println("列表");
        System.out.println(commodityIntroDTOList);
        if(commodityIntroDTOList==null || commodityIntroDTOList.size()==0){
            //然后如果为空的话就去类别表中查询一级的id对比，然后的到级别对象列表
            List<CommodityCategoryDTO> childCategorys = commodityMapper.getChildCategoryByPlevel(category_id);
            ArrayList<String> child_category_ids = new ArrayList<>();
            for (CommodityCategoryDTO childCategory:
                 childCategorys) {
                // 直接拿到二级分类的级别id
                child_category_ids.add(childCategory.getId());
            }
            System.out.println("二级分类ID");
            System.out.println(child_category_ids);
            commodityIntroDTOList = commodityMapper.getCommodityIntroByCategoryIdList(child_category_ids,offset,pageSize,ranking);
        }

        return commodityIntroDTOList;
    }

    public List<CommodityIntroDTO> getCommodityIntroByCategoryId_Kw(String kw, String category_id, int pageNum, int pageSize,String ranking) {
        int offset = pageSize*(pageNum-1);
        // 判断category_id是一级还是二级
        CommodityCategoryDTO categoryById = commodityMapper.getCategoryById(category_id);
        ArrayList<String> child_category_ids = new ArrayList<>();
        if(categoryById.getLevel().equals(String.valueOf(1))){
            //如果是一级则需要查询其二级的ids
            List<CommodityCategoryDTO> childCategoryByPlevel = commodityMapper.getChildCategoryByPlevel(category_id);
            System.out.println("关键字和分类");
            System.out.println(childCategoryByPlevel);
            for (CommodityCategoryDTO commodityCategory :
                    childCategoryByPlevel) {
                child_category_ids.add(commodityCategory.getId());
            }
        }else{
            // 如果是二级
            child_category_ids.add(category_id);
        }
        System.out.println("关键字和分类");
        System.out.println(kw);
        System.out.println(child_category_ids);
        List<CommodityIntroDTO> commodityIntroDTOList = commodityMapper.getCommodityIntrosByCategoryId_Kw(kw,child_category_ids,offset,pageSize,ranking);
        return commodityIntroDTOList;
    }

    public Category getCategoryById2(String id){return commodityMapper.getCategoryById2(id);}


    public Boolean setComments(Comment comment){return commodityMapper.setComments(comment);}

    public List<Comment> findCommentsByCommodity_Id(String commodity_id){return commodityMapper.findCommentsByCommodity_Id(commodity_id);}

    public Comment findCommentsById(String id){return commodityMapper.findCommentsById(id);}

    public List<Specification> getAllSpecification(String comodity_id){return commodityMapper.getAllSpecification(comodity_id);}

    public ArrayList<Specification_priceDTO> getSpecification_priceByCommodity_id(String commodity_id) {
        // 通过商品id获取到商品的规格组合
        List<Specification_price> specification_priceList = commodityMapper.getSpecification_priceByCommodity_id(commodity_id);
        ArrayList<Specification_priceDTO> specification_priceDTOArrayList = new ArrayList<>();
        for (Specification_price specification_price :
                specification_priceList) {
            List<String> specification_ids = Arrays.asList(specification_price.getSpecification_ids().split(","));
            // 规格组合列表
            ArrayList<Specification> specifications = new ArrayList<>();
            for (String specification_id:
                    specification_ids) {
                Specification specification = commodityMapper.getBySpecificationId(specification_id.trim());
                specifications.add(specification);
            }

            Specification_priceDTO specification_priceDTO = new Specification_priceDTO(specification_price);
            specification_priceDTO.setSpecifications(specifications);
            specification_priceDTOArrayList.add(specification_priceDTO);
        }
        return specification_priceDTOArrayList;
    }
}
