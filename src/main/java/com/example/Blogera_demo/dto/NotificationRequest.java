package com.example.Blogera_demo.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String token;
    private String title;
    private String body;

}
    