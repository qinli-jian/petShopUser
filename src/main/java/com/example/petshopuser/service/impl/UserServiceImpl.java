package com.example.petshopuser.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopuser.entity.User;
import com.example.petshopuser.mapper.UserMapper;
import com.example.petshopuser.service.IUserService;
import com.example.petshopuser.utils.SnowflakeIdWorker;
import com.example.petshopuser.utils.Utils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;


    private final SnowflakeIdWorker snowflakeIdWorker;


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(SnowflakeIdWorker snowflakeIdWorker) {
        this.snowflakeIdWorker = snowflakeIdWorker;
    }

    public List<User> getAllUser(){
        List<User> allUser = userMapper.getAllUser();
        return allUser;
    }

    public User getUserById(String id){
        return userMapper.getUserById(id);
    }

    public User findUserByPhone(String phone) {
        User user = userMapper.findUserByPhone(phone);
        return user;
    }
    public User findUserByAccount(String account){
        return userMapper.findUserByAccount(account);
    }

    public boolean sendPhoneCode(String phone) {
        String randomCode = Utils.generateRandomCode(6);
        // 验证码保存到mysql
        // 首先查看是否有这个手机的记录
        int phoneCodeByPhone = userMapper.find_phoneCodeByPhone(phone);
        if(phoneCodeByPhone>0){
            userMapper.update_phoneCode(randomCode,phone);
        }else{
            String id = String.valueOf(snowflakeIdWorker.nextId());
            userMapper.insert_phoneCode(id,randomCode,phone);
        }

        boolean b = aliyunSms(phone, randomCode);
        return b;
    }

    private boolean aliyunSms(String phone,String code){
        System.out.println("==============发送前");
        System.out.println(code);
        try{
            // 使用阿里云sms发送短信
            Config config = new Config()
                    // 您的
                    .setAccessKeyId(System.getenv("AliyunSMSAccessKeyId"))
                    // 您的
                    .setAccessKeySecret(System.getenv("AliyunSMSAccessKeySecret"));
            // 访问的域名
            config.endpoint = "dysmsapi.aliyuncs.com";
            Client client = new Client(config);
            SendSmsRequest smsrequest = new SendSmsRequest();
            smsrequest.phoneNumbers = phone; // 该参数值为假设值，请您根据实际情况进行填写
            smsrequest.signName = "aicqmodel"; // 该参数值为假设值，请您根据实际情况进行填写
            smsrequest.templateCode = "SMS_272955176";
            smsrequest.templateParam = "{code:"+code+"}";
            SendSmsResponse response = client.sendSms(smsrequest);
            System.out.println(new Gson().toJson(response.body));
            if(response.body.getCode().equals("OK")){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    public int check_phoneCode(String phone,String code) {
        String store_code = userMapper.get_phoneCodeByPhone(phone);
        // 获取当前时间戳
        long currentTimestamp = System.currentTimeMillis();

    // 将当前时间戳转换为Timestamp对象
        Timestamp current = new Timestamp(currentTimestamp);

    // 获取数据库中的时间戳
        Timestamp time = userMapper.get_phoneCodeTimeByPhone(phone);

        // 计算时间差（单位：毫秒）
        long difference = currentTimestamp - time.getTime();

        // 将时间差转换为分钟
        long minutes = difference / (60 * 1000);

        // 判断时间差是否超过五分钟
        if (minutes > 5) {
            System.out.println("超过五分钟");
            return -1;
        }
        if(store_code.equals(code)){
            return 1;
        }
        return 0;
    }

    public int addUser(String username, String sex,String age, String phone, String code, String password) {
        String id = String.valueOf(snowflakeIdWorker.nextId());
        final String defualt_avatar = "avatar_user.jpg";
        // 在这里进行密码加密
        String encodePassword = bCryptPasswordEncoder.encode(password);
        User user = new User(id,defualt_avatar, username, sex, age,phone,phone, encodePassword, "");
        int b = userMapper.addUser(user);
        if(b!=1){
            return 0;
        }
        return b;
    }
}
