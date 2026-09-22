package com.season.semiproject.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.season.semiproject.vo.UserProfile;

@Repository
public class UserProfileDAO {
    @Autowired
    SqlSession session;

    private static final String NAMESPACE = "com.season.semiproject.mapper.UserProfileMapper";

    public UserProfileDAO(SqlSession sqlSession) {
        this.session = sqlSession;
    }

    public void insertUserProfile(UserProfile userProfile) {
        session.insert(NAMESPACE + ".insertUserProfile", userProfile);
    }

    public String getProfileImage(String userId) {
        String profileImage = session.selectOne(NAMESPACE + ".getProfileImage", userId);
        if (profileImage == null || profileImage.isEmpty()) {
            return "/images/기본프로필.png";
        }
        return profileImage;
    }

    public void updateUserProfile(UserProfile userProfile) {
        session.update(NAMESPACE + ".updateUserProfile", userProfile);
    }

    public String getUserById(String userId) {
        return session.selectOne(NAMESPACE + ".getUserById", userId);
    }
}
