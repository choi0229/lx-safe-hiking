package com.season.semiproject.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.season.semiproject.vo.Comment;
import com.season.semiproject.vo.Community;
import com.season.semiproject.vo.User;

@Repository
public class CommunityDAO {
   
   @Autowired
   SqlSession session;
   
   public List<Community> getCommunityList() throws Exception {
      return session.selectList("getCommunityList");
   }
   
   public List<Community> getSafeList() throws Exception {
         return session.selectList("getSafeList");
   }

   
   public Community getCommunityById(int communityPk) {
      return session.selectOne("getCommunityById", communityPk);
   }
   
   
   public void createCommunity(Community vo) {
      session.insert("createCommunity", vo);
   }
   
   
   public List<Community> getLatestCommunities() {
       return session.selectList("getLatestCommunities");
   }
   
   public User getUserById(String userId) {
        return session.selectOne("UserMapper.getUserById", userId);
    }

   public int edit(Community vo) {
      return session.update("edit", vo);
   }

   
   public void deleteCommunity(int CommunityPk) {
      session.delete("deleteCommunity", CommunityPk);
   }

   public List<Community> getCommentsByCommunityId(int CommunityPk) {
      return session.selectList("getCommentsByCommunityId", CommunityPk);
   }

   
   public void createComment(Comment vo) {
      session.insert("createComment", vo);
   }

   
   public void deleteComment(int commentPk) {
      session.delete("deleteComment", commentPk);
   }
   
   
   public int increaseLikes(Community vo) {
       return session.insert("increaseLikes", vo);
   }
   
   
   public int decreaseLikes(Community vo) {
       return session.delete("decreaseLikes", vo);
   }

   public int isLiked(Community vo) {
       int count = session.selectOne("isLiked", vo);
       return count;
   }
   
   public int getLikes(int communityPk) {
       return session.selectOne("getLikes", communityPk);
   }

   public List<Community> getCommunityByCourse(String courseName){
	   return session.selectList("getCommunityByCourse",courseName);
   }
   
}
