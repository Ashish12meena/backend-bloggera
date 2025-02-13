package com.example.Blogera_demo.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "post")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    private String id;

    private String userId;

    @Size(max = 50)
    private String title;

    @Size(max = 100)
    private String subheading;

    
    private String content;

    private long likeCount;
    private long commentCount;
    private String postImage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

