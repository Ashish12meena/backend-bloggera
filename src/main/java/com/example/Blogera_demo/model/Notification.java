package com.example.Blogera_demo.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.example.Blogera_demo.utility.ActionType;

import lombok.Data;

@Data
@Document(collection = "notification")
public class Notification {

       @Id
    private String id;  
    private String recipientUserId; 
    private String senderUserId;  

    @Field("actionType")
    private ActionType actionType;  // Enum (LIKE, COMMENT, FOLLOW)

    private String postId; // (For LIKE & COMMENT)
    private String commentId; // (For COMMENT)
    private String message;  
    private boolean isViewed; 
    private LocalDateTime createdAt = LocalDateTime.now();
}
