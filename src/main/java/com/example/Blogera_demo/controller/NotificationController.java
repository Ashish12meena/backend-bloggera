package com.example.Blogera_demo.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Blogera_demo.dto.FindByUserId;
import com.example.Blogera_demo.dto.NotificationRequest;
import com.example.Blogera_demo.dto.SendNotificationDto;
import com.example.Blogera_demo.service.FCMService;
import com.example.Blogera_demo.service.NotificationService;

@CrossOrigin(origins = "*", allowedHeaders = "Authorization")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;


     @Autowired
    FCMService fcmService;

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody NotificationRequest request) {
        try {
            String response = fcmService.sendNotification(request.getToken(), request.getTitle(), request.getBody());
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body("Error sending notification: " + e.getMessage());
        }
    }

    @PostMapping("/unread-notification")
    public List<SendNotificationDto> getNotificationData(@RequestBody FindByUserId findByUserId) {
        return notificationService.getNotificationData(findByUserId.getUserId());
    }   

    @PostMapping("/markAsViewed")
    public ResponseEntity<?> markAsViewed(@RequestBody FindByUserId findByUserId) {
        notificationService.markNotificationsAsViewed(findByUserId.getUserId());
        return ResponseEntity.ok().body("ok");
    }
}