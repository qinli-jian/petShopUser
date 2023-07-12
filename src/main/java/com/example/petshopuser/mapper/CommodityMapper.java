package com.example.petshopuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.Specification;
import com.example.petshopuser.entity.Specification_price;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommodityMapper extends BaseMapper<Commodity> {
    Commodity getCommodityById(String id);

    List<Specification_price> getAllByCommodityId(String commodity_id);

    Specification getBySpecificationId(String id);
}
