package com.example.Blogera_demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.example.Blogera_demo.dto.NotificationDto;
import com.example.Blogera_demo.dto.SendNotificationDto;
import com.example.Blogera_demo.model.Notification;
import com.example.Blogera_demo.model.Post;
import com.example.Blogera_demo.model.User;
import com.example.Blogera_demo.repository.NotificationRepo;
import com.example.Blogera_demo.serviceInterface.NotificationServiceInterface;


@Service
public class NotificationService implements NotificationServiceInterface {

    @Autowired
    NotificationRepo notificationRepo;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void markNotificationsAsViewed(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("recipientUserId").is(userId).and("isViewed").is(false));

        Update update = new Update();
        update.set("isViewed", true);

        mongoTemplate.updateMulti(query, update, Notification.class);
    }

    @Override
    public Notification addNotification(NotificationDto notificationDto) {
        // Fetch post details
        Post post = postService.getPostsByPostId(notificationDto.getPostId());
        if (post == null) {
            throw new IllegalArgumentException("Post not found with ID: " + notificationDto.getPostId());
        }
    
    
        // Create notification
        Notification notification = new Notification();
        notification.setActionType(notificationDto.getActionType());
        notification.setSenderUserId(notificationDto.getUserId());
        notification.setRecipientUserId(post.getUserId());
        notification.setPostId(post.getId());
        
        notification.setViewed(false);
        notification.setCreatedAt(LocalDateTime.now());
    
        // Set message based on action type
        switch (Objects.requireNonNull(notification.getActionType(), "Action type is required")) {
            case LIKE:
                notification.setMessage( " liked your post");
                break;
            case COMMENT:
                notification.setMessage(" commented: " + notificationDto.getCommentText());
                break;
            case FOLLOW:
                notification.setMessage(" started following you");
                break;
            default:
                throw new IllegalArgumentException("Invalid action type");
        }
    
        // Save and return notification
        return notificationRepo.save(notification);
    }
    
    @Override
    public void deleteNotification(String senderUserId, String postId) {
        notificationRepo.deleteBySenderUserIdAndPostId(senderUserId, postId);
    }

    @Override
    public List<Notification> getUnreadNotifications(String recipientUserId) {
        return notificationRepo.findByRecipientUserIdAndIsViewed(recipientUserId, false);
    }

    @Override
    public List<SendNotificationDto> getNotificationData(String userId) {
        List<Notification> listOfUnreadNotifications = getUnreadNotifications(userId);
        if (listOfUnreadNotifications == null || listOfUnreadNotifications.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> senderUserIds = listOfUnreadNotifications.stream()
                .map(Notification::getSenderUserId)
                .filter(Objects::nonNull) // Avoid null values
                .collect(Collectors.toSet());


        Set<String> postIds = listOfUnreadNotifications.stream()
                .map(Notification::getPostId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());


        List<Post> posts = postService.getPostsByPostIds(new ArrayList<>(postIds));
        if (posts == null)
            posts = Collections.emptyList();

        List<User> users = userService.getUsersByIds(new ArrayList<>(senderUserIds));

        if (users == null)
            users = Collections.emptyList();
        Map<String, String> postIdTopostImageMap = posts.stream()
                .collect(Collectors.toMap(
                        Post::getId,
                        post -> Objects.toString(post.getPostImage(), ""), 
                        (a, b) -> a 
                ));

        Map<String, String> userIdToUsernameMap = users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> Objects.toString(user.getUsername(), "Unknown"), // Replace null with "Unknown"
                        (a, b) -> a // Keep the first value in case of duplicates
                ));

        Map<String, String> userIdToUserImageMap = users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> Objects.toString(user.getProfilePicture(), "")));

        return listOfUnreadNotifications.stream()
                .filter(Objects::nonNull) // Prevent null notifications
                .map(notification -> {
                    SendNotificationDto dto = new SendNotificationDto();
                    dto.setActionType(notification.getActionType());
                    dto.setCommentId(notification.getCommentId()); // Safe if notification is not null
                    dto.setCommentText(notification.getMessage());
                    dto.setNotificationId(notification.getId());
                    dto.setPostId(notification.getPostId());
                    dto.setViewed(notification.isViewed());

                    dto.setUsername(userIdToUsernameMap.get(notification.getSenderUserId()));
                    dto.setUserImage(userIdToUserImageMap.get(notification.getSenderUserId()));
                    dto.setPostImage(postIdTopostImageMap.get(notification.getPostId()));

                    return dto;
                })
                .collect(Collectors.toList());

    }

}
