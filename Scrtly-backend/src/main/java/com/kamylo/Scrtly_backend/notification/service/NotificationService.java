package com.kamylo.Scrtly_backend.notification.service;

import com.kamylo.Scrtly_backend.notification.web.dto.NotificationDto;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.notification.domain.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void createOrUpdateNotification(Long userId, Long postId, NotificationType type, String triggeringUserName);
    void decrementNotification(Long recipientId, Long postId, NotificationType type);
    void deleteNotificationsByPost(PostEntity post);
    Page<NotificationDto> getOwnerNotifications(String username, Pageable pageable);
    void deleteNotification(Long id, String username);
}
