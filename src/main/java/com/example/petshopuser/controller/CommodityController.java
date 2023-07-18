package com.example.petshopuser.controller;

import com.example.petshopuser.common.Constants;
import com.example.petshopuser.entity.*;
import com.example.petshopuser.entity.DTO.CommodityCategoryDTO;
import com.example.petshopuser.entity.Commodity;
import com.example.petshopuser.entity.DTO.Specification_priceDTO;
import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.entity.Specification;
import com.example.petshopuser.entity.Specification_price;
import com.example.petshopuser.service.impl.CommodityServiceImpl;
import com.example.petshopuser.service.impl.UserServiceImpl;
import com.example.petshopuser.utils.SnowflakeIdWorker;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.petshopuser.entity.DTO.CommodityIntroDTO;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/commodity")
public class CommodityController {
    private final SnowflakeIdWorker snowflakeIdWorker;
    @Resource
    private CommodityServiceImpl commodityService;

    @Resource
    private UserServiceImpl userService;

    public CommodityController(SnowflakeIdWorker snowflakeIdWorker) {
        this.snowflakeIdWorker = snowflakeIdWorker;
    }
    //商品详情查询
    @GetMapping("/details")
    public ReturnObj getCommodityById(@RequestParam String id) {
        ReturnObj returnObj = new ReturnObj();
        Map<String, Object> data = new HashMap<>();
        Map<String,Object> commodityO = new HashMap<>();
        Commodity commodity = commodityService.getCommodityById(id); //根据商品id查询商品

        if (commodity != null){
            commodityO.put("id", commodity.getId());
            commodityO.put("name", commodity.getName());
            commodityO.put("imgs",commodity.getImgs().split(", "));

            System.out.println(commodity.getCategory_id());
            Category category = commodityService.getCategoryById2(commodity.getCategory_id());  //获取商品一级分类
            if(category!=null){
                Category category1 = commodityService.getCategoryById2(category.getP_level_id()); //获取商品二级分类
                if(category1!=null){   //添加商品分类
                    data.put("categoryLevel"+category.getLevel(), category.getCategory_name());
                    data.put("categoryLevel"+category1.getLevel(), category1.getCategory_name());
                }else {
                    returnObj.setCode("500");
                    returnObj.setMsg("该分类不存在");
                    return returnObj;
                }
            }else{
                returnObj.setCode("500");
                returnObj.setMsg("该分类不存在");
                return returnObj;
            }



        }
        else {
            returnObj.setCode("500");
            returnObj.setMsg("error");
            return returnObj;
        }

        List<Specification_price> specification_prices = commodityService.getAllByCommodityId(commodity.getId()); //根据商品id、获取规格及价格
        if(specification_prices==null){
            returnObj.setCode("500");
            returnObj.setMsg("error");
            return returnObj;
        }
        Integer total_sales_volume = 0;
        List<Map<String, Object>> objects = new ArrayList<>();
        for (Specification_price specification_price : specification_prices) {
            if (specification_price.getSpecification_ids() != null) {

                String[] temp_ids = specification_price.getSpecification_ids().split(","); //分割规格id
                Map<String,Object> dataDTO = new HashMap<>();
                dataDTO.put("specifications_id", specification_price.getId());
                dataDTO.put("price", specification_price.getPrice());
                dataDTO.put("sales_volume",specification_price.getSales_volume());
                total_sales_volume+=specification_price.getSales_volume();
                dataDTO.put("inventory",specification_price.getInventory());
                List<Map<String,Object>> specifications =new ArrayList<>();
                for (String temp_id : temp_ids) {
                    Map<String,Object> specificationDTO = new HashMap<>();
                    Specification specification_item = commodityService.getBySpecificationId(temp_id.trim());
                    if(specification_item!=null){
                        specificationDTO.put("specification_type", specification_item.getType());
                        specificationDTO.put("specification_name", specification_item.getSpecification_name());
                    }
                   specifications.add(specificationDTO);
                }
                dataDTO.put("specifications", specifications);
                objects.add(dataDTO);
            }
        }
        commodityO.put("specification_price",objects);
        commodityO.put("total_sales_volume", total_sales_volume);
        data.put("commodity", commodityO);
        returnObj.setData(data);
        returnObj.setMsg("success");
        returnObj.setCode("200");
        return returnObj;
    }

    // 时间降序，价格升序或降序(P_ASC，P_DES),销量升序或降序(S_ASC，S_DES)
    @GetMapping("/search")
    public ReturnObj search(@RequestParam(value = "kw", required = false) String kw,@RequestParam(value = "category_id", required = false)String category_id,
                            @RequestParam(value = "pageNum")Integer pageNum,@RequestParam(value = "pageSize")Integer pageSize,
                            @RequestParam(value = "ranking",required = false) String ranking){
        ReturnObj returnObj = new ReturnObj();
        if(pageNum<=0 || pageSize <=0){
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("params error");
            return returnObj;
        }
        System.out.println(kw);
        System.out.println(category_id);
        System.out.println(pageNum);
        System.out.println(pageSize);
        System.out.println(ranking);
        List<CommodityIntroDTO> commodityList = null;
        //
        if((kw==null || kw.isEmpty()) && (category_id==null || category_id.isEmpty())){
            System.out.println("======所有");
            // 搜索全部的，返回商品的简介对象
            commodityList = commodityService.getAllCommodityIntro(pageNum,pageSize,ranking);
        } else if ((kw!=null || !kw.isEmpty()) && (category_id==null ||  category_id.isEmpty())) {
            // 根据kw关键字模糊
            System.out.println("======关键字");
            commodityList = commodityService.getCommodityIntroByKW(kw,pageNum,pageSize,ranking);
        }else if ((kw==null || kw.isEmpty()) && (category_id!=null || !category_id.isEmpty())) {
            System.out.println("======分类");
            // 根据类别的ID进行搜索，这个ID有可能是一级或者是二级的
            commodityList = commodityService.getCommodityIntroByCategoryId(category_id,pageNum,pageSize,ranking);
        }else{
            // 根据关键词和类别的ID进行搜索
            commodityList = commodityService.getCommodityIntroByCategoryId_Kw(kw,category_id,pageNum,pageSize,ranking);
        }
        returnObj.setCode(Constants.CODE_200);
        returnObj.setMsg("success");
        returnObj.setData(commodityList);
        return returnObj;
    }
    //获取所有分类
    @GetMapping("/getAllCategory")
    public ReturnObj getAllCategory(){
        ReturnObj returnObj = new ReturnObj();
        try {
            List<CommodityCategoryDTO> categorys = commodityService.getAllCategory();  //获取所有分类列表
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
            returnObj.setData(categorys);
        }catch (Exception e){
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
            System.out.println("========getAllCategory");
            System.out.println(e);
        }
        return returnObj;
    }
    //根据商品id获取所有该商品规格
    @GetMapping("/getAllSpecification")
    public ReturnObj getAllSpecification(@RequestParam(value = "comodity_id") String comodity_id){
        ReturnObj returnObj =new ReturnObj();
        try {
            List<Specification> specifications = commodityService.getAllSpecification(comodity_id);
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
            returnObj.setData(specifications);
        }catch (Exception e){
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
            System.out.println("========getAllSpecification");
            System.out.println(e);
        }
        return returnObj;
    }


    // 上传商品id进行查询商品的规格组合，以便进行选择{ [{},{}组合对象列表],price,imgs}
    @GetMapping("/commodity_specification_price")
    public ReturnObj commodity_specification_price(@RequestParam(value = "commodity_id") String commodity_id){
        ReturnObj returnObj = new ReturnObj();
        try{
            ArrayList<Specification_priceDTO> specification_priceDTO = commodityService.getSpecification_priceByCommodity_id(commodity_id);
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
            returnObj.setData(specification_priceDTO);
        }catch (Exception e){
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }
        return returnObj;
    }

//    @PostMapping("/comment/set")
//    public ReturnObj setComments(@RequestBody Map<String,String> request_form){
//        ReturnObj returnObj = new ReturnObj();
//        Comment comment = new Comment();
//        String id = String.valueOf(snowflakeIdWorker.nextId());
//        comment.setId(id);
//        comment.setUser_id(request_form.get("user_id"));
//        comment.setCommodity_id(request_form.get("commodity_id"));
//        comment.setReplyComments_id(request_form.get("replyComments_id"));
//        comment.setContent(request_form.get("content"));
//        comment.setImgs(request_form.get("imgs"));
//        comment.setRating(Integer.valueOf(request_form.get("rating")));
//        if(commodityService.setComments(comment)){
//            returnObj.setCode("200");
//            returnObj.setMsg("success");
//            returnObj.setData(commodityService.findCommentsById(id));
//        }
//        else{
//            returnObj.setData(false);
//            returnObj.setCode("500");
//            returnObj.setMsg("error");
//        }
//        return returnObj;
//    }
//    @PostMapping("/comment/get")
//    public ReturnObj findCommentsByCommodity_Id(@RequestBody Map<String,String> request_form){
//        ReturnObj returnObj = new ReturnObj();
//        String commodity_id = request_form.get("commodity_id");
//        List<Comment> comments = commodityService.findCommentsByCommodity_Id(commodity_id);
//        if(comments!=null){
//            Map<String,Object> data = new HashMap<>();
//            List<Map<String,Object>> dataList = new ArrayList<>();
//            for(Comment comment:comments){
//                User user = userService.getUserById(comment.getUser_id());
//                data.put("user_name", user.getName());
//                data.put("user_avatar", user.getAvatar());
//                data.put("commodity_id", comment.getCommodity_id());
//                data.put("replyComments_id", comment.getReplyComments_id());
//                data.put("content",comment.getContent());
//                data.put("rating", comment.getRating());
//                String[] imgs = comment.getImgs().split(",");
//                data.put("imgs", imgs);
//                data.put("time", comment.getCreateTime());
//                dataList.add(data);
//            }
//            returnObj.setData(dataList);
//            returnObj.setMsg("success");
//            returnObj.setCode("200");
//        }
//        else {
//            returnObj.setCode("500");
//            returnObj.setMsg("error");
//            returnObj.setData(false);
//        }
//        return returnObj;
//    }

}
