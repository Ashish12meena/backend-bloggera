package com.example.Blogera_demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Blogera_demo.dto.AddFollower;
import com.example.Blogera_demo.dto.GetBYEmailAndUserId;
import com.example.Blogera_demo.model.Follow;
import com.example.Blogera_demo.model.User;
import com.example.Blogera_demo.service.FollowService;
import com.example.Blogera_demo.service.UserService;

@CrossOrigin(origins = "*", allowedHeaders = "Authorization")
@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;
    
    @PostMapping("/add")
    public ResponseEntity<?> addFollower(@RequestBody AddFollower addFollower){
        Follow follow = followService.addFollower(addFollower);
        if (follow!=null) {
            return ResponseEntity.ok().body("Follower added");
        }else{
            return ResponseEntity.badRequest().body("Follower not added");
        }
        
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeFollower(@RequestBody AddFollower addFollower){
        
        followService.removeFollower(addFollower);
       return ResponseEntity.ok().body("Follower removed");
    }

    @PostMapping("/status")
    public ResponseEntity<?> status(@RequestBody  GetBYEmailAndUserId request){
        User user = userService.getUserByEmail(request.getEmail());
         
       boolean status = followService.isFollowing(request.getUserId(),user.getId());
       return ResponseEntity.ok().body("Follower removed "+status);
    }
}
