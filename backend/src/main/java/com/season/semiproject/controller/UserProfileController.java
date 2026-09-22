package com.season.semiproject.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.season.semiproject.dao.UserProfileDAO;
import com.season.semiproject.vo.UserProfile;

@RestController
public class UserProfileController {
	private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
	
    @Autowired
    private UserProfileDAO userProfileDAO;

    private static final String DEFAULT_PROFILE_IMAGE = "/images/기본프로필.png";
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/api/uploadprofile")
    public ResponseEntity<Map<String, Object>> uploadProfileImage(
            @RequestParam("profileImage") MultipartFile file,
            @RequestParam("userId") String userId) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", "파일이 업로드되지 않았습니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            String userDir = UPLOAD_DIR + userId + "/";
            File userDirectory = new File(userDir);
            if (!userDirectory.exists()) {
                userDirectory.mkdirs();
            }

            String fileName = userId + "_" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String filePath = Paths.get(userDir, fileName).toAbsolutePath().toString();
            logger.info("File path: {}", filePath);

            File dest = new File(filePath);
            file.transferTo(dest);

            String fileUrl = "http://localhost:9000/uploads/" + userId + "/" + fileName;
            logger.info("File URL: {}", fileUrl);
            
            UserProfile existingProfile = new UserProfile(userId, fileUrl);
            String existingImage = userProfileDAO.getProfileImage(userId);

            if (existingImage == null) {
                userProfileDAO.insertUserProfile(existingProfile);
            } else {
                userProfileDAO.updateUserProfile(existingProfile);
            }

            response.put("message", "프로필 사진이 성공적으로 업로드되었습니다.");
            response.put("imageUrl", fileUrl);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("error", "파일 업로드 중 오류가 발생했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/getProfileImage")
    public ResponseEntity<Map<String, Object>> getProfileImage(@RequestParam("userId") String userId) {
        Map<String, Object> response = new HashMap<>();
        String profileImage = userProfileDAO.getProfileImage(userId);

        if (profileImage == null || profileImage.isEmpty()) {
            response.put("imageUrl", "http://localhost:9000" + DEFAULT_PROFILE_IMAGE);
        } else {
            response.put("imageUrl", profileImage);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
