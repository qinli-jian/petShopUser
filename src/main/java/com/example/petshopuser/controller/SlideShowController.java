package com.example.petshopuser.controller;


import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.service.impl.SlideShowServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/slide")
public class SlideShowController {
    @Resource
    private SlideShowServiceImpl slideShowService;
    @PostMapping("/slideShow")
    public ReturnObj returnSlideShow(){
        ReturnObj returnObj = new ReturnObj();
            returnObj.setMsg("success");
            returnObj.setCode("200");
            returnObj.setData(slideShowService.getAllSlideShow());
        return returnObj;
    }

}
