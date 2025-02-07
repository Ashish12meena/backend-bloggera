package com.example.Blogera_demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCardDetails {
    private String username;
    private String profilePicture;
    LocalDateTime LocalDateTime;
    private String message;
}
