package com.example.petshopuser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopuser.entity.User;
import com.example.petshopuser.mapper.UserMapper;
import com.example.petshopuser.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    public List<User> getAllUser(){
        List<User> allUser = userMapper.getAllUser();
        return allUser;
    }

    public User findUserByPhone(String phone) {
        userMapper.findUserByPhone(phone);
    }
}
