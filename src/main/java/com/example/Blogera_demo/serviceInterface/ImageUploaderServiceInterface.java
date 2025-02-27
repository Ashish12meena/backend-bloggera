package com.example.Blogera_demo.serviceInterface;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploaderServiceInterface {
    String uploadImage(MultipartFile image);
    List<String> allFiles();
    String preSignedUrl(String filename);
    String getPublicUrl(String filename);
}
