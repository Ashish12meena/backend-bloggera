package com.example.Blogera_demo.service;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FCMService {
     public String sendNotification(String token, String title, String body) throws ExecutionException, InterruptedException {
        System.out.println("In Service");
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                        
                .build();

        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }
}
