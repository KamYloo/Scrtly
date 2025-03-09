package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.entity.enums.NotificationType;

public interface NotificationService {
    void createOrUpdateNotification(Long userId, Long postId, NotificationType type, String triggeringUserName);
}
