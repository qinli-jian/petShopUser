package com.example.petshopuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopuser.entity.DTO.ShopCarDTO;
import com.example.petshopuser.entity.ShopCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShopCartMapper extends BaseMapper<ShopCart> {

    List<ShopCart> getList(String user_id);
}
