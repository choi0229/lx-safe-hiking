package com.season.semiproject.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.season.semiproject.vo.User;

@Repository
public class UserDAO {
	
	@Autowired
	SqlSession session;
	
	public void createUser(User user) {
		session.insert("createUser", user);
	}

	public User tryLogin(String userId, String userPw) {
		User user = new User(userId, userPw);
		return session.selectOne("tryLogin",user);
	}
	
    public boolean isIdTaken(String userId) {
        Integer count = session.selectOne("isIdTaken", userId);
        return count != null && count > 0;
    }

    public boolean isEmailTaken(String userEmail) {
        Integer count = session.selectOne("isEmailTaken", userEmail);
        return count != null && count > 0;
    }

    public boolean isNickNameTaken(String userNickName) {
        Integer count = session.selectOne("isNickNameTaken", userNickName);
        return count != null && count > 0;
    }
    
    public List<String> getAllUserIds() {
        return session.selectList("getAllUserIds");
    }

    public String getFcmTokenByUserId(String userId) {
        return session.selectOne("getFcmTokenByUserId", userId);
    }
    
    public User findUserById(String userId) {
        return session.selectOne("findUserById", userId);
    }
    
    public String getUserNickNameByUserId(String userId) {
        return session.selectOne("getUserNickNameByUserId", userId);
    }
}
