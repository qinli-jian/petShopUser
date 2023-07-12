package com.example.petshopuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopuser.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<User> getAllUser();

    User getUserById(String id);

    User findUserByPhone(String phone);

    User findUserByAccount(String account);

    int insert_phoneCode(String id,String code,String phone);

    int find_phoneCodeByPhone(String phone);

    int update_phoneCode(String code,String phone);

    String get_phoneCodeByPhone(String phone);

    int addUser(User user);

    Timestamp get_phoneCodeTimeByPhone(String phone);
}
