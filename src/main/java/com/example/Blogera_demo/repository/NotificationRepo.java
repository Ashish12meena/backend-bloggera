package com.example.Blogera_demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.Blogera_demo.model.Notification;

public interface NotificationRepo extends MongoRepository<Notification, String> {
    void deleteBySenderUserIdAndPostId(String senderUserId, String postId);

    List<Notification> findByRecipientUserIdAndIsViewed(String recipientUserId, boolean isViewed);

}
