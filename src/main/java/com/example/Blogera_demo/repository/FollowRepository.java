package com.example.Blogera_demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import com.example.Blogera_demo.model.Follow;


@Repository
public interface FollowRepository extends  MongoRepository<Follow,String> {
    void deleteByFollowerIdAndFollowingId(String followerId, String followingId);
    boolean existsByFollowerIdAndFollowingId(String followerId, String followingId);
}
