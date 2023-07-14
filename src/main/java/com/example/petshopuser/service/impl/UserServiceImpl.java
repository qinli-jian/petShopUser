package com.example.petshopuser.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopuser.entity.Address;
import com.example.petshopuser.entity.User;
import com.example.petshopuser.mapper.UserMapper;
import com.example.petshopuser.service.IUserService;
import com.example.petshopuser.utils.SnowflakeIdWorker;
import com.example.petshopuser.utils.Utils;
import com.google.gson.Gson;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            smsrequest.signName = "顶针的动物朋友"; // 该参数值为假设值，请您根据实际情况进行填写
            smsrequest.templateCode = "SMS_282495027";
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

    public User getUserInfoByPhone(String phone) {
        User user = userMapper.getUserInfoByPhone(phone);
        return user;
    }

    public User getUserInfoByUserId(String user_id) {
        User user = userMapper.getUserInfoByUserId(user_id);
        return user;
    }

    public int modifyProfile(Map<String, String> user_info) {
        User user = null;
        if(user_info.get("avatar").isEmpty()){
            user_info.put("avatar","avatar_user.jpg");
        }
        if(user_info.get("id").isEmpty() || user_info.get("name").isEmpty() || user_info.get("sex").isEmpty() || user_info.get("age").isEmpty() || user_info.get("account").isEmpty() || user_info.get("phone").isEmpty() ){
            return -1;
        }
        try{
            user = new User(user_info.get("id"),user_info.get("avatar"),user_info.get("name"),user_info.get("sex"),user_info.get("age"),user_info.get("account"),user_info.get("phone"),user_info.get("address"));
        }catch (Exception e){
            System.out.println("提取数据失败");
            return 0;
        }
        int flag = userMapper.update_userProfile(user);
        if(flag==1){
            return 1;
        }else{
            return 0;
        }
    }

    public int resetPassword(String account, String password) {
        String encodePassword = bCryptPasswordEncoder.encode(password);
        int f = userMapper.update_resetUserPassword(account,encodePassword);
        return f;
    }

    public int addAddress(String user_id, String address) {

        // 把address json字符串转为对象
        Gson gson = new Gson();
        HashMap<String, String> address_obj = new HashMap<>();
        address_obj = gson.fromJson(address, address_obj.getClass());
        String id = String.valueOf(snowflakeIdWorker.nextId());
        address_obj.put("id",id);
//        System.out.println("---");
//        System.out.println(address_obj.get("defaultAddress"));

        // 进行判断关键的东西不能为空
        if(address_obj.get("id").isEmpty() || address_obj.get("addressee").isEmpty() || address_obj.get("province").isEmpty()
                || address_obj.get("city").isEmpty() || address_obj.get("county").isEmpty()
                || address_obj.get("postcode").isEmpty() || address_obj.get("phone").isEmpty()){
            return -1;
        }

        Address new_address = new Address(address_obj);
        // 根据user_id进行插入new_address
        int flag = userMapper.insert_userAddress(user_id,new_address);

        return flag;

    }

    public int updateAddress(String user_id, String address) {
        Gson gson = new Gson();
        HashMap<String, String> address_obj = new HashMap<>();
        address_obj = gson.fromJson(address, address_obj.getClass());
        Address new_address = new Address(address_obj);
        System.out.println("新地址");
        System.out.println(new_address);
        int flag = userMapper.updateAddress(user_id,new_address);

        return flag;

    }

    public List<Address> getAddressListByUserId(String user_id) {

        List<Address> addressList = userMapper.getAddressListByUserId(user_id);

        return addressList;

    }

    public int delete_address(String user_id, String address_id) {

        int flag = userMapper.delete_address(user_id,address_id);
        return flag;
    }

    public String save_avatar(String userId, MultipartFile image) {
        // 保存文件到本地
        String filename = null;
        try {
            String savePath = "E:\\作业文件\\实训\\code\\petShopUser\\src\\main\\resources\\static"; // 设置保存路径
            String imageName = image.getOriginalFilename();
            System.out.println(imageName);
            int end_name_idx = imageName.lastIndexOf(".");
            String extension = imageName.substring(end_name_idx);
            filename = String.valueOf(snowflakeIdWorker.nextId())+extension;
            File destFile = new File(savePath , filename);
            image.transferTo(destFile);
            int f = userMapper.save_avatar(userId,filename);
            if(f!=1){
                return null;
            }
        } catch (Exception e) {
            // 处理文件保存失败的逻辑
            e.printStackTrace();
            return null;
        }
        return filename;
    }
}
