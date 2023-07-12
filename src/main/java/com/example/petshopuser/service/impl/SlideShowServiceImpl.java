package com.example.petshopuser.service.impl;

import com.example.petshopuser.entity.SlideShow;
import com.example.petshopuser.mapper.SlideShowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SlideShowServiceImpl {
    @Resource
    private SlideShowMapper slideShowMapper;
    public List<SlideShow> getAllSlideShow(){
        return slideShowMapper.getAllSideShow();
    }
}
