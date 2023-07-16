package com.example.petshopuser.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private String id;
    private String replyComments_id;
    private String user_id;
    private String commodity_id;
    private String content;
    private String imgs;
    private Date createTime;
    private  Integer rating;
}
