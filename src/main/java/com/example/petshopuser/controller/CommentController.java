package com.example.petshopuser.controller;

import com.example.petshopuser.common.Constants;
import com.example.petshopuser.entity.Comment;
import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.entity.User;
import com.example.petshopuser.repository.CommentRepository;
import com.example.petshopuser.service.impl.CommentServiceImpl;
import com.example.petshopuser.service.impl.UserServiceImpl;
import com.example.petshopuser.utils.Utils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentServiceImpl commentService;

    @Resource
    private UserServiceImpl userService;

    @GetMapping("/test")
    public ReturnObj text(){
        ReturnObj returnObj = new ReturnObj();
        commentService.testGetData();
        return returnObj;
    }

    @GetMapping("/listbyCommodityId")
    public ReturnObj getCommentListByCommodityId(@RequestParam(value = "commodity_id") String commodity_id,@RequestParam(value = "pageNum") int pageNum,@RequestParam(value = "pageSize") int pageSize){
        ReturnObj returnObj = new ReturnObj();

        List<Comment> comments = commentService.getAllByCommodityId(commodity_id,pageNum,pageSize);
        if(comments!=null){
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
            returnObj.setData(comments);
        }else{
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }
        return returnObj;
    }

    // 添加评论
    @PostMapping("/addcomment")
    public ReturnObj addcomment(@RequestBody Map<String,Object> addCommentInfo) {
        ReturnObj returnObj = new ReturnObj();
        String user_id;
        String content;
        ArrayList<String> images;
        Integer rating;
        String reply_to_comment_id = "-1";
        String reply_to_username = "";
        try {
            user_id = (String) addCommentInfo.get("user_id");
            content = (String) addCommentInfo.get("content");
            rating = (Integer) addCommentInfo.get("rating");
            images = (ArrayList<String>) addCommentInfo.get("images");

            if (addCommentInfo.containsKey("reply_to_comment_id")) {
                reply_to_comment_id = (String) addCommentInfo.get("reply_to_comment_id");
                if (!reply_to_comment_id.equals("-1")) {
                    rating = -1;
                } else {
                    reply_to_username = (String) addCommentInfo.get("reply_to_username");
                }
            }
//            Integer rating_int = Integer.valueOf(rating);
        } catch (Exception e) {
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("参数错误");
            return returnObj;
        }
        if (!addCommentInfo.containsKey("commodity_id")) {
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("params error");
            return returnObj;
        }
        String commodity_id = (String) addCommentInfo.get("commodity_id");
        String timestamp = Utils.nowTime();

        User user = userService.getUserById(user_id);
        if (user == null) {
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("params error");
            return returnObj;
        }
        Comment comment = new Comment(user_id,"1", user.getName(), user.getAvatar(), commodity_id, content, images, timestamp, rating, reply_to_comment_id, reply_to_username);
        // 写入数据库
        int flag = commentService.addComment(comment);
        if (flag == 1) {
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
        } else {
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }
        return returnObj;
    }

    // 获取平均评分
    @GetMapping("/avgCommodityRating")
    public ReturnObj avgCommodityRating(@RequestParam(value = "commodity_id")String commodity_id){
        ReturnObj returnObj = new ReturnObj();
        try{
            double avgRating = commentService.getAvgCommodityRating(commodity_id);
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
            HashMap<String, Double> data = new HashMap<String, Double>();
            data.put("avgRating",avgRating);
            returnObj.setData(data);
        }catch (Exception e){
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }
        return returnObj;
    }

    // 获取评价总数
    @GetMapping("/sumComment")
    public ReturnObj sumComment(@RequestParam(value = "commodity_id")String commodity_id){
        ReturnObj returnObj = new ReturnObj();
        long sumNum = commentService.countCommentNum(commodity_id);
        if(sumNum==-1){
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }else{
            returnObj.setCode(Constants.CODE_200);
            HashMap<String, Long> data = new HashMap<>();
            data.put("count",sumNum);
            returnObj.setData(data);
            returnObj.setMsg("success");
        }
        return returnObj;
    }

}
