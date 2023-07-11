package com.example.petshopuser.controller;

import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.entity.User;
import com.example.petshopuser.utils.Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.petshopuser.service.impl.UserServiceImpl;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserServiceImpl userService;
    @PostMapping("/login")
    public ReturnObj login(@RequestBody Map<String,String> login_form){
        ReturnObj returnObj = new ReturnObj();
        List<User> allUser = userService.getAllUser();
        for(int i=0;i<allUser.size();i++){
            if(allUser.get(i).getAccount().equals(login_form.get("account"))){
                if(allUser.get(i).getPassword().equals(login_form.get("password"))){
                    returnObj.setMsg("登陆成功");
                    returnObj.setCode("200");
                    returnObj.setData(Utils.generateToken(allUser.get(i),"user"));
                }
                else{
                    returnObj.setMsg("密码错误,请重试");
                    returnObj.setCode("500");
                }
                return returnObj;
            }
        }
        returnObj.setMsg("没有该用户,请注册！");
        returnObj.setCode("500");
        return returnObj;
    }

    @PostMapping("/register")
    public ReturnObj register(@RequestBody Map<String,String> register_form){
        ReturnObj returnObj = new ReturnObj();
//        String
        String phone = register_form.get("phone");
        String password = register_form.get("password");
        String repassword = register_form.get("repassword");
        if(!repassword.equals(password)){
            returnObj.setCode("500");
            return returnObj;
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



        return returnObj;
    }

}
