package com.example.Blogera_demo.dto;

import lombok.Data;

@Data
public class CommentDto {
    private String postId;
    private String userId;
    private String message;
}
