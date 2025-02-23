package com.example.Blogera_demo.dto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPostDto {
    private String  userId;
    private String title;
    private String content;
    private List<String> category;
    private MultipartFile postImage;

}