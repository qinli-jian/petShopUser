package com.example.petshopuser.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("commodityComments")
public class Comment {
    @Id
    private String id;
    @Field
    private String user_id;
    @Field
    private String commodity_id;
    @Field
    private String content;
    @Field
    private ArrayList<String> images;
    @Field(value = "createTime")
    private String createTime;
    @Field
    private  Integer rating;
    @Field
    private  String reply_to_comment_id;

    private  ArrayList<Comment> subComments;

    public Comment(String user_id, String commodity_id, String content, ArrayList<String> images, String createTime, Integer rating, String reply_to_comment_id) {
        this.user_id = user_id;
        this.commodity_id = commodity_id;
        this.content = content;
        this.images = images;
        this.createTime = createTime;
        this.rating = rating;
        this.reply_to_comment_id = reply_to_comment_id;
    }
}
