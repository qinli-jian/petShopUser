package com.example.petshopuser.controller;

import com.example.petshopuser.entity.ReturnObj;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/login")
    public ReturnObj login(@RequestBody Map<String,String> login_form){
        ReturnObj returnObj = new ReturnObj();
        
        return returnObj;
    }

    @PostMapping("/register")
    public ReturnObj register(@RequestBody Map<String,String> register_form){
        ReturnObj returnObj = new ReturnObj();

        return returnObj;
    }

}
