package com.example.Blogera_demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllPostCardDetails {
    private String postId;
    private String username;
    private String profilePicture;
    private String postContent;
    private String postTitle;
    private long likeCount;
    private long commentCount;
    private String postImage;
    private boolean likeStatus;
}
