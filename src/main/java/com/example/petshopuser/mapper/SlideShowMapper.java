package com.example.petshopuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petshopuser.entity.SlideShow;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SlideShowMapper extends BaseMapper<SlideShow> {
     List<SlideShow> getAllSideShow();
}
