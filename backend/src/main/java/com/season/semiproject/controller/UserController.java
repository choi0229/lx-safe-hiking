package com.season.semiproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.season.semiproject.dao.UserDAO;
import com.season.semiproject.vo.User;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api")
@RestController
public class UserController {
    @Autowired
    UserDAO dao;

    @PostMapping("/login")
    public User tryLogin(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String userPw = payload.get("userPw");
        User user = dao.tryLogin(userId, userPw);
        System.out.println(user);
        return user;
    }
    
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            dao.createUser(user);

            response.put("success", true);
            response.put("userNickname", user.getUserNickName());
            response.put("userGender", user.getUserGender());
            response.put("userId", user.getUserId());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "회원가입에 실패했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/checkId")
    public ResponseEntity<Map<String, Object>> checkId(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        String userId = payload.get("userId");
        
        boolean isIdTaken = dao.isIdTaken(userId);
        response.put("success", !isIdTaken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/checkEmail")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        String userEmail = payload.get("userEmail");
        
        boolean isEmailTaken = dao.isEmailTaken(userEmail);
        response.put("success", !isEmailTaken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/checkNickName")
    public ResponseEntity<Map<String, Object>> checkNickName(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        String userNickName = payload.get("userNickName");
        
        boolean isNickNameTaken = dao.isNickNameTaken(userNickName);
        response.put("success", !isNickNameTaken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/userInfo/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
 		User user = dao.findUserById(userId);
 		if (user != null) {
 			return ResponseEntity.ok(user);
 		} else {
 			return ResponseEntity.notFound().build();
 		}
 	}
}
