package com.example.petshopuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopuser.entity.ShopCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface ShopCartMapper extends BaseMapper<ShopCart> {

    List<ShopCart> getList(String user_id);

    int batchInsertToShopCar(String id,String user_id,ArrayList<Map<String, String>> infoList);
}
