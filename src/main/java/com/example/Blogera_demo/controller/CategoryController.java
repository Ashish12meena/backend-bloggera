package com.example.Blogera_demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Blogera_demo.dto.GetCategories;
import com.example.Blogera_demo.service.CategoryService;

@CrossOrigin(origins = "*", allowedHeaders = "Authorization")
@RestController
@RequestMapping("/api/category")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;


    @PostMapping("/list")
    public List<GetCategories> getListOfCategories(){
         return categoryService.getCategories();
    }

}
