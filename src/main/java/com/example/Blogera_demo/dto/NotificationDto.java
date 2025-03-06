package com.example.Blogera_demo.dto;

import com.example.Blogera_demo.utility.ActionType;

import lombok.Data;


@Data
public class NotificationDto {
    private String postId;
    private String userId;
    private String commentText;
    ActionType actionType;
}
