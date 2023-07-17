package com.example.petshopuser.repository;

import com.example.petshopuser.entity.Comment;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    @Aggregation(pipeline = {
            "{$match: {commodity_id : ?0,reply_to_comment_id:\"-1\"}}",
            "{$group: {_id: null, averageRating: {$avg: \"$rating\"}}}"
    })
    List<CommentAggregationResult> calculateAverageRatingByCommodityId(String commodityId);

    public static class CommentAggregationResult {
        @Field("averageRating")
        private Double averageRating;

        public Double getAverageRating() {
            return averageRating;
        }
    }
}
