package com.example.Blogera_demo.serviceInterface;

import com.example.Blogera_demo.dto.AddFollower;
import com.example.Blogera_demo.model.Follow;

public interface FollowServiceInterface {
    Follow addFollower(AddFollower addFollower);
    void removeFollower(AddFollower addFollower);
    boolean isFollowing(String followerId, String followingId);
}
