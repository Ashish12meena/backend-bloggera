package com.example.Blogera_demo.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Blogera_demo.dto.FindUserByEmial;
import com.example.Blogera_demo.dto.GetBYEmailAndUserId;
import com.example.Blogera_demo.dto.TokenFcmDto;
import com.example.Blogera_demo.dto.UserDetails;
import com.example.Blogera_demo.service.UserService;

@CrossOrigin(origins = "*", allowedHeaders = "Authorization")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/userdetails")
    public ResponseEntity<?> getLoggedInUser(@RequestBody GetBYEmailAndUserId request){
        
        return userService.getUserCardData(request);
    }

    @PostMapping("/validateToken")
    public UserDetails validateToken(@RequestBody FindUserByEmial findUserByEmial){
        

        return userService.validateToken(findUserByEmial);
    }

    @PostMapping("/save-fcm-token")
    public ResponseEntity<?> saveFCMToken(@RequestBody TokenFcmDto fcmTokenDto){
        userService.saveFCMToken(fcmTokenDto);
        return ResponseEntity.ok().body("Token saved");

    }

    @GetMapping("/allusers")
    public List<String> getAllUserId(){
        return userService.getAllUserId();
    }
}