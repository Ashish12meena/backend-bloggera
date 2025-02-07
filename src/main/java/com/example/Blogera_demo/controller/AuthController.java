package com.example.Blogera_demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.example.Blogera_demo.dto.GetAllPostCardDetails;
import com.example.Blogera_demo.dto.Login;
import com.example.Blogera_demo.model.User;
import com.example.Blogera_demo.service.AuthService;
import com.example.Blogera_demo.service.PostService;

// @CrossOrigin("*")
@RestController
@RequestMapping("auth/users")
public class AuthController {

    @Autowired  
    private AuthService userService;

    @Autowired
    private PostService postService;


    @PostMapping("/register")
    public ResponseEntity<?> createUsers(@RequestBody User user){
        
        
        return userService.createUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
       return userService.loginUser(login);
    }


    @PostMapping("/cardDetails")
    public List<GetAllPostCardDetails> getCartDetails(){
        
        return postService.getCardDetails();
    }
    
    @GetMapping
    public String dolike(){
        return "Hello sir";
    }
}
