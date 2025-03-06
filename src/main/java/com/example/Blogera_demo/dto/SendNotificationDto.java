package com.example.Blogera_demo.dto;

import com.example.Blogera_demo.utility.ActionType;

import lombok.Data;

@Data
public class SendNotificationDto {
    private String notificationId;
    private String username;
    private ActionType actionType;
    private String postId;
    private String userImage;
    private String postImage;
    private String commentText; 
    private String commentId;
    private boolean isViewed;
}
