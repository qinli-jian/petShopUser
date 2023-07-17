package com.example.petshopuser.service.impl;

import com.example.petshopuser.entity.DTO.CommodityCategoryDTO;
import com.example.petshopuser.entity.DTO.CommodityIntroDTO;
import com.example.petshopuser.entity.DTO.ShopCarDTO;
import com.example.petshopuser.entity.DTO.Specification_priceDTO;
import com.example.petshopuser.entity.ShopCart;
import com.example.petshopuser.entity.Specification;
import com.example.petshopuser.entity.Specification_price;
import com.example.petshopuser.mapper.CommodityMapper;
import com.example.petshopuser.mapper.ShopCartMapper;
import com.example.petshopuser.utils.SnowflakeIdWorker;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ShopCartServiceImpl {

    @Resource
    private ShopCartMapper shopCartMapper;

    private final SnowflakeIdWorker snowflakeIdWorker;


    @Resource
    private CommodityMapper commodityMapper;

    public ShopCartServiceImpl(SnowflakeIdWorker snowflakeIdWorker) {
        this.snowflakeIdWorker = snowflakeIdWorker;
    }

    public List<ShopCarDTO> getList(String user_id) {

        List<ShopCart> shopCarList = shopCartMapper.getList(user_id);
        List<ShopCarDTO> shopCarDTOList = new ArrayList<>();
        for (ShopCart shopCart:
             shopCarList) {
            ShopCarDTO shopCarDTO = new ShopCarDTO();
            shopCarDTO.setId(shopCart.getId());
            shopCarDTO.setAmount(shopCart.getAmount());
            shopCarDTO.setUser_id(shopCart.getUser_id());
            shopCarDTO.setCreateTime(shopCart.getCreateTime());
            // 根据商品ID获取简介
            CommodityIntroDTO commodityIntroDTO = commodityMapper.getCommodityIntroById(shopCart.getCommodity_id());
            shopCarDTO.setCommodityInfo(commodityIntroDTO);
            // 根据商品ID获取一级二级分类
            List<CommodityCategoryDTO> categoryList = new ArrayList<>();
            String categoryId = commodityIntroDTO.getCategoryId();
            CommodityCategoryDTO commodityCategoryDTO = commodityMapper.getCategoryById(categoryId);
            categoryId = commodityCategoryDTO.getP_level_id();
            System.out.println(commodityCategoryDTO);
            categoryList.add(commodityCategoryDTO);
            CommodityCategoryDTO p_commodityCategoryDTO = commodityMapper.getCategoryById(categoryId);
            categoryList.add(p_commodityCategoryDTO);
            commodityIntroDTO.setCategory(categoryList);
            // 根据规格组合id获取组合对象
            Specification_price specification_price = commodityMapper.getSpecification_priceById(shopCart.getSpecification_price_id());
//            System.out.println("规格");
//            System.out.println(specification_price);
//            System.out.println(shopCart.toString());
            if(specification_price==null){
                continue;
            }
            List<String> specification_ids = Arrays.asList(specification_price.getSpecification_ids().split(","));

                // 规格对象列表
            ArrayList<Specification> specifications = new ArrayList<>();
            for (String specification_id:
                 specification_ids) {
                Specification specification = commodityMapper.getBySpecificationId(specification_id);
                specifications.add(specification);
            }
            Specification_priceDTO specification_priceDTO = new Specification_priceDTO();
            specification_priceDTO.setId(specification_price.getId());
            specification_priceDTO.setCommodity_id(specification_price.getCommodity_id());
            specification_priceDTO.setSpecifications(specifications);
            specification_priceDTO.setPrice(specification_price.getPrice());
            specification_priceDTO.setInventory(specification_price.getInventory());
            specification_priceDTO.setImage(specification_price.getImg());

            shopCarDTO.setSpecification_price(specification_priceDTO);

            shopCarDTOList.add(shopCarDTO);
        }
        return shopCarDTOList;
    }


    public int insert_shopCart(String user_id,ArrayList<Map<String, String>> infoList) {
        String id = String.valueOf(snowflakeIdWorker.nextId());
        int f = shopCartMapper.batchInsertToShopCar(id,user_id,infoList);
        return f;
    }
}
