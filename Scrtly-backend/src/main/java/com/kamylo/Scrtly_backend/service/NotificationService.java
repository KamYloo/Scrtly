package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.NotificationDto;
import com.kamylo.Scrtly_backend.entity.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void createOrUpdateNotification(Long userId, Long postId, NotificationType type, String triggeringUserName);
    Page<NotificationDto> getOwnerNotifications(String username, Pageable pageable);
    void deleteNotification(Long id, String username);
}
