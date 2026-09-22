package com.season.semiproject.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${FIREBASE_SERVICE_ACCOUNT_PATH:}")
    private String serviceAccountPath;

    @PostConstruct
    public void initializeFirebaseApp() {
        if (serviceAccountPath.isBlank()) {
            log.warn("FIREBASE_SERVICE_ACCOUNT_PATH is not set; FCM push notifications are disabled");
            return;
        }
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }
        try (InputStream serviceAccount = new FileInputStream(serviceAccountPath)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            log.info("FirebaseApp initialized");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize Firebase from " + serviceAccountPath, e);
        }
    }
}
