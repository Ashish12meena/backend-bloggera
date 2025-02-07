package com.example.Blogera_demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Blogera_demo.dto.FindUserByEmial;
import com.example.Blogera_demo.dto.UserDetails;
import com.example.Blogera_demo.service.UserService;

@CrossOrigin(origins = "*", allowedHeaders = "Authorization")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/userdetails")
    public ResponseEntity<?> getLoggedInUser(@RequestBody FindUserByEmial userEmail){
        
        return userService.getUserCardData(userEmail.getEmail());
    }

    @PostMapping("/validateToken")
    public UserDetails validateToken(@RequestBody FindUserByEmial findUserByEmial){
        

        return userService.validateToken(findUserByEmial);
    }
}