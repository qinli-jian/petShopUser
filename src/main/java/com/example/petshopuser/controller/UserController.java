package com.example.petshopuser.controller;

import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.entity.User;
import com.example.petshopuser.utils.Utils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.petshopuser.service.impl.UserServiceImpl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserServiceImpl userService;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @PostMapping("/login")
    public ReturnObj login(@RequestBody Map<String,String> login_form){
        ReturnObj returnObj = new ReturnObj();
        System.out.println(login_form);
        User user = userService.findUserByAccount(login_form.get("account"));
        if(user!=null){
            if(bCryptPasswordEncoder.matches(login_form.get("password"),user.getPassword())){
                returnObj.setMsg("登陆成功");
                returnObj.setCode("200");
                String token = Utils.generateToken(user,"user");
                Map<String, String> data = new HashMap<>();
                data.put("token",token);
                returnObj.setData(data);
            }
            else{
                returnObj.setMsg("密码错误,请重试");
                returnObj.setCode("500");
            }
            return returnObj;
        }
        returnObj.setMsg("没有该用户,请注册！");
        returnObj.setCode("500");
        return returnObj;
    }

    @PostMapping("/register")
    public ReturnObj register(@RequestBody Map<String,String> register_form){
        ReturnObj returnObj = new ReturnObj();
//        String
        String username = register_form.get("username");
        String sex = register_form.get("sex");
        String age = register_form.get("age");
        String phone = register_form.get("phone");
        String code = register_form.get("code");
        String password = register_form.get("password");
        String repassword = register_form.get("repassword");

        if(username.isEmpty() || sex.isEmpty() ||phone.isEmpty() || code.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            returnObj.setCode("500");
            return returnObj;
        }
        if(!repassword.equals(password)){
            returnObj.setCode("500");
            return returnObj;
        }
        // 查询是否已经存在用户
        User user = userService.findUserByPhone(phone);
        if(user!=null){
            returnObj.setCode("500");
            returnObj.setMsg("user exist");
            return returnObj;
        }
        // 现在进行验证验证码
        int check_flag = userService.check_phoneCode(phone,code);
        if(check_flag==0){
            returnObj.setMsg("code error");
            returnObj.setCode("500");
            return returnObj;
        } else if (check_flag==-1) {
            returnObj.setMsg("code time out");
            returnObj.setCode("500");
            return returnObj;
        }

        // 插入用户
        int b = userService.addUser(username, sex, age, phone, code, password);
        if(b==1){
            returnObj.setCode("200");
            returnObj.setMsg("success");
        }else{
            returnObj.setCode("500");
            returnObj.setMsg("register failed");
        }
        return returnObj;
    }



    @PostMapping("/get_phone_code")
    public ReturnObj get_phone_code(@RequestBody Map<String,String> register_form){
        ReturnObj returnObj = new ReturnObj();
        String phone = register_form.get("phone");
        // 检查用户是否存在
        User user = userService.findUserByPhone(phone);
        if(user!=null){
            returnObj.setCode("500");
            returnObj.setMsg("user exist");
            return returnObj;
        }

        // 用户不存在就可以发送验证码
        boolean b = userService.sendPhoneCode(phone);
        if(b){
            returnObj.setCode("200");
            returnObj.setMsg("success");
        }else{
            returnObj.setCode("500");
            returnObj.setMsg("failed");
        }
        return returnObj;
    }

}
