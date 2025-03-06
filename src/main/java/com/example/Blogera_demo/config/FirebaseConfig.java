package com.example.Blogera_demo.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws IOException {
        System.out.println("In config");
        FileInputStream serviceAccount = new FileInputStream(
                Objects.requireNonNull(getClass().getClassLoader().getResource("firebase-service-account.json")).getFile());
        System.out.println("service account "+serviceAccount);
           

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {

            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase initialized successfully!");
        }else{
            System.out.println("Firebase already initialized!");
        }
    }
}
