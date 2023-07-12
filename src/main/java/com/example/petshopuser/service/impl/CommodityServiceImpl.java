package com.example.petshopuser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.Specification;
import com.example.petshopuser.entity.Specification_price;
import com.example.petshopuser.mapper.CommodityMapper;
import org.springframework.stereotype.Service;
import com.example.petshopuser.entity.DTO.CommodityCategoryDTO;
import com.example.petshopuser.entity.DTO.CommodityIntroDTO;
import com.example.petshopuser.service.ICommodityService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements ICommodityService {

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
    public List<CommodityIntroDTO> getAllCommodityIntro(int pageNum,int pageSize) {
        int offset = pageSize*(pageNum-1);
        List<CommodityIntroDTO> commodityIntroDTOList = commodityMapper.getAllCommodityIntro(offset,pageSize);
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

    public List<CommodityIntroDTO> getCommodityIntroByKW(String kw,int pageNum,int pageSize) {
        int offset = pageSize*(pageNum-1);
        List<CommodityIntroDTO> commodityIntroDTOList = commodityMapper.getCommodityIntroByKW(kw,offset,pageSize);

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

    public List<CommodityIntroDTO> getCommodityIntroByCategoryId(String category_id,int pageNum,int pageSize) {
        int offset = pageSize*(pageNum-1);
        List<CommodityIntroDTO> commodityIntroDTOList = null;
        //首先在商品表中通过对比级别id进行查询
        commodityIntroDTOList = commodityMapper.getCommodityIntroByCategoryId(category_id,offset,pageSize);
        if(commodityIntroDTOList==null){
            //然后如果为空的话就去类别表中查询一级的id对比，然后的到级别对象列表
            List<CommodityCategoryDTO> childCategorys = commodityMapper.getChildCategoryByPlevel(category_id);
            ArrayList<String> child_category_ids = new ArrayList<>();
            for (CommodityCategoryDTO childCategory:
                 childCategorys) {
                // 直接拿到二级分类的级别id
                child_category_ids.add(childCategory.getId());
            }
            commodityIntroDTOList = commodityMapper.getCommodityIntroByCategoryIdList(child_category_ids,offset,pageSize);
        }

        return commodityIntroDTOList;
    }

    public List<CommodityIntroDTO> getCommodityIntroByCategoryId_Kw(String kw, String child_category_ids, int pageNum, int pageSize) {
        int offset = pageSize*(pageNum-1);
        List<CommodityIntroDTO> commodityIntroDTOList = commodityMapper.getCommodityIntrosByCategoryId_Kw(kw,child_category_ids,offset,pageSize);
        return commodityIntroDTOList;
    }
}
