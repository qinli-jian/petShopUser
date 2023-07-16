package com.example.petshopuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopuser.entity.Address;
import com.example.petshopuser.entity.Ip_address;
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

    User getUserInfoByPhone(String phone);

    User getUserInfoByUserId(String user_id);

    int update_userProfile(User user);

    int update_resetUserPassword(String account, String password);

    int insert_userAddress(String user_id, Address new_address);

    Boolean setIP(String id,String user_id,String ip,String ip_address);

    Ip_address getIP(String user_id);
  
    int updateAddress(String user_id, Address new_address);

    List<Address> getAddressListByUserId(String user_id);

    int delete_address(String user_id, String address_id);

    int save_avatar(String user_id, String filename);

    List<Ip_address> getLastTwoIP(String user_id);
}
