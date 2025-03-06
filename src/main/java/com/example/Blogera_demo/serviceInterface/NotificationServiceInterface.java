package com.example.Blogera_demo.serviceInterface;

import java.util.List;

import com.example.Blogera_demo.dto.NotificationDto;
import com.example.Blogera_demo.dto.SendNotificationDto;
import com.example.Blogera_demo.model.Notification;

public interface NotificationServiceInterface {
    Notification addNotification(NotificationDto notificationDto);
    void deleteNotification(String senderUserId, String postId);
    List<Notification> getUnreadNotifications(String recipientUserId);
    List<SendNotificationDto> getNotificationData(String userId);
}
