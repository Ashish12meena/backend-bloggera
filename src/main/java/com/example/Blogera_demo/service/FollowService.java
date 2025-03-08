package com.example.Blogera_demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.example.Blogera_demo.dto.AddFollower;
import com.example.Blogera_demo.model.Follow;

import com.example.Blogera_demo.repository.FollowRepository;
import com.example.Blogera_demo.serviceInterface.FollowServiceInterface;

@Service
public class FollowService implements FollowServiceInterface{
    private final UserService userService;

    @Autowired
    FollowRepository followRepository;


    
    @Autowired
    MongoTemplate mongoTemplate;
    
    public FollowService(@Lazy UserService userService) {
        this.userService = userService;
    }
    

    @Override
    public Follow addFollower(AddFollower addFollower) {
        Follow follow = new Follow();
        follow.setFollowingId(addFollower.getFollowedUserId());
        follow.setFollowerId(addFollower.getLoggedUserId());

        try {
            follow = followRepository.save(follow);
            userService.incrementFollowerCount(addFollower.getFollowedUserId());

            userService.incrementFollowingCount(addFollower.getLoggedUserId());
        } catch (Exception e) {
            throw new RuntimeException("Error while adding follower: " + e.getMessage());
        }
        return follow;
    }

    @Override
    public void removeFollower(AddFollower addFollower) {
        try {
            followRepository.deleteByFollowerIdAndFollowingId(addFollower.getLoggedUserId(), addFollower.getFollowedUserId());
            userService.decrementFollowerCount(addFollower.getFollowedUserId());
            userService.decrementFollowingCount(addFollower.getLoggedUserId());
        } catch (Exception e) {
            throw new RuntimeException("Error while removing follower: " + e.getMessage());
        }
    }

    @Override
    public boolean isFollowing(String followerId, String followingId) {
        boolean status = followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
        return status;
    }

     
    
}
