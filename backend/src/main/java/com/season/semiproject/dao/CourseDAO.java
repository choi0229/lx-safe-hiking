package com.season.semiproject.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.season.semiproject.vo.Course;
import com.season.semiproject.vo.Review;

@Repository
public class CourseDAO {
    @Autowired
    SqlSession session;
    
    public Course getCourseById(Integer courseId) {
        return session.selectOne("getCourse", courseId);
     }

    public List<Course> getAllCourses() {
        return session.selectList("getAllCourses");
    }
    
    public Course byName(String courseName) {
    	return session.selectOne("getCourseByName",courseName);
    }

    public List<Review> getCourseReviews(int courseId) {
        return session.selectList("getCourseReviews", courseId);
    }

    public List<Integer> getCourseRatings(int courseId) {
        return session.selectList("getCourseRatings", courseId);
    }

    public String getCourseNameById(int courseId) {
        return session.selectOne("getCourseNameById", courseId);
    }
    
    public List<Review> searchCourseReviews(int courseId, String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("courseId", courseId);
        params.put("query", query);
        return session.selectList("searchCourseReviews", params);
    }
}
