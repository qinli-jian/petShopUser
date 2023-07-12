package com.example.petshopuser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.Specification;
import com.example.petshopuser.entity.Specification_price;
import com.example.petshopuser.entity.User;
import com.example.petshopuser.mapper.CommodityMapper;
import com.example.petshopuser.mapper.UserMapper;
import com.example.petshopuser.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CommodityServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private CommodityMapper commodityMapper;

    public Commodity getCommodityById(String id){
        return commodityMapper.getCommodityById(id);
    }

    public List<Specification_price> getAllByCommodityId(String commodity_id){
        return commodityMapper.getAllByCommodityId(commodity_id);
    }
    public Specification getBySpecificationId(String id){
        return commodityMapper.getBySpecificationId(id);
    }
}
