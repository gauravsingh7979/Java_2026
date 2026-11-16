package com.findit.service;

import com.findit.dto.common.PageResponse;
import com.findit.dto.notification.NotificationResponse;
import com.findit.entity.Notification;
import com.findit.entity.User;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    Notification createNotification(User user, String title, String message, String type, String targetUrl);

    PageResponse<NotificationResponse> getUserNotifications(Long userId, Pageable pageable);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    long getUnreadCount(Long userId);
}
