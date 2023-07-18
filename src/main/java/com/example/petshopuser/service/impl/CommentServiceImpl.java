package com.example.petshopuser.service.impl;

import com.example.petshopuser.entity.Comment;
import com.example.petshopuser.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CommentServiceImpl {

    @Autowired
    private MongoTemplate mongoTemplate;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void testGetData(){
        List<Comment> all = mongoTemplate.findAll(Comment.class);
        System.out.println("评论查询测试");
        System.out.println(all);
    }

    // 根据商品ID 进行查询评论
    public List<Comment> getAllByCommodityId(String commodity_id,int pageNum,int pageSize) {
        System.out.println("分页");
        System.out.println(pageNum);
        System.out.println(pageSize);
        List<Comment> comments = null;
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("commodity_id").is(commodity_id).and("reply_to_comment_id").is("-1"));
            query.with(Sort.by(Sort.Order.desc("createTime")));
            query.skip((pageNum - 1) * pageSize).limit(pageSize);
            comments = mongoTemplate.find(query, Comment.class);
//            List<Comment> allSubComment = new ArrayList<>();
            for (Comment comment :
                    comments) {
                //查询这个的所有回复的评论
                List<Comment> sub_comments = getSubComments(comment.getId());
//                allSubComment.addAll((ArrayList<Comment>) sub_comments);
                comment.setSubComments((ArrayList<Comment>) sub_comments);
            }
            
        } catch (Exception e) {
            System.out.println("查询商品评论出错");
            System.out.println(e);
            return null;
        }
        return comments;
    }

    public List<Comment> getSubComments(String comment_id){
        Query sub_query = new Query();
        sub_query.addCriteria(Criteria.where("reply_to_comment_id").is(comment_id));
        sub_query.with(Sort.by(Sort.Order.desc("createTime")));
        List<Comment> sub_comments = mongoTemplate.find(sub_query, Comment.class);

        if (sub_comments != null && !sub_comments.isEmpty()) {
            List<Comment> newComments = new ArrayList<>(); // 临时集合用于保存新的子评论
            Iterator<Comment> iterator = sub_comments.iterator();
            while (iterator.hasNext()) {
                Comment comment = iterator.next();
                List<Comment> subComments = getSubComments(comment.getId());
                newComments.addAll(subComments);
            }
            sub_comments.addAll(newComments); // 将新的子评论添加到原始集合中
        }

        return sub_comments;
    }

    public int addComment(Comment comment) {
        try{
            mongoTemplate.save(comment);
        }catch (Exception e){
            System.out.println(e);
            return 0;
        }
        return 1;
    }

    public double getAvgCommodityRating(String commodity_id) {

        List<CommentRepository.CommentAggregationResult> results = commentRepository.calculateAverageRatingByCommodityId(commodity_id);
        if (!results.isEmpty()) {
            CommentRepository.CommentAggregationResult result = results.get(0);
            return result.getAverageRating();
        }
        return 0.0;
    }

    public long countCommentNum(String commodity_id) {
        long commodityComments;
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("commodity_id").is(commodity_id).and("reply_to_comment_id").is("-1"));
            commodityComments = mongoTemplate.count(query, "commodityComments");
        } catch (Exception e) {
            return -1;
        }
        return commodityComments;
    }
}
