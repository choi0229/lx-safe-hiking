package com.season.semiproject.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.season.semiproject.dao.CommunityDAO;
import com.season.semiproject.vo.Comment;
import com.season.semiproject.vo.Community;
import com.season.semiproject.vo.User;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class CommunityController {
   @Autowired
   private CommunityDAO dao;

   @GetMapping("/communityList")
   public List<Community> communityList() throws Exception {
      List<Community> list = dao.getCommunityList();
      System.out.println(list);
      return list;
   }
   
   @PostMapping("/safeList")
   public List<Community> safeList() throws Exception {
      List<Community> list = dao.getSafeList();
      System.out.println(list);
      return list;
   }

   @GetMapping("/detail/{id}")
   public Community getCommunityDetail(@PathVariable("id") int communityPk) {
      System.out.println("지금 누른 게시판 아이디: " + communityPk);
      return dao.getCommunityById(communityPk);
   }

   @PostMapping("/community/add")
   public void createCommunity(@RequestBody Community vo) {
        System.out.println(vo.userNickName);
        dao.createCommunity(vo);
    }
   
      @PostMapping("/community/upload")
       public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
           String folderPath = "C:/lx/spring/workspace/semiproject/frontend/public/images/";
           String fileName = file.getOriginalFilename();
           System.out.println("사진들어옴");
           File saveFile = new File(folderPath + fileName);
           try {
               file.transferTo(saveFile);
               
               String imagePath = "/images/" + fileName;
               return ResponseEntity.ok(imagePath);
           } catch (IOException e) {
               e.printStackTrace();
               return ResponseEntity.status(500).body("File upload failed");
           }
       }

   @GetMapping("/comments/{id}")
   public List<Community> getComments(@PathVariable("id") int communityPk) {
      System.out.println("지금 누른 게시판 아이디(댓글용): " + communityPk);
      return dao.getCommentsByCommunityId(communityPk);
   }
   
   @PostMapping("/comments/add")
   public ResponseEntity<String> addComment(@RequestBody Comment comment) {
       try {
           dao.createComment(comment);
           return ResponseEntity.ok("댓글이 추가되었습니다.");
       } catch (Exception e) {
           e.printStackTrace();
           return ResponseEntity.status(500).body("댓글 추가 실패");
       }
   }

   @PostMapping("/like")
   public List<Community> likePost(@RequestBody Community vo) throws Exception {
      int isLiked = dao.isLiked(vo);
      if (isLiked == 0) {
         dao.increaseLikes(vo);
      } else {
         dao.decreaseLikes(vo);
      }
      return dao.getCommunityList();
   }
   
   @PostMapping("/comCourse")
   public List<Community> getCommunityByCourse(@RequestBody Community vo){
	   String courseName = vo.courseName;
	   System.out.println(dao.getCommunityByCourse(courseName));
	   return dao.getCommunityByCourse(courseName);
   }
}
