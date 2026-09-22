package com.season.semiproject.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.season.semiproject.vo.Review;

@Repository
public class ReviewDAO {
    @Autowired
    SqlSession session;

    public void createReview(Review review) {
        session.insert("mapper-review.insertReview", review);
    }

    public void updateReview(Review review) {
        session.update("mapper-review.updateReview", review);
    }

    public void deleteReview(int reviewId) {
        session.delete("mapper-review.deleteReview", reviewId);
    }

    public List<Review> searchReviews(int courseId, String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("courseId", courseId);
        params.put("query", query);
        return session.selectList("mapper-review.searchReviews", params);
    }
    
    public Review getReviewById(int reviewId) {
        return session.selectOne("mapper-review.getReviewById", reviewId);
    }
}
