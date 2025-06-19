package com.kamylo.Scrtly_backend.notification.listeners;

import com.kamylo.Scrtly_backend.notification.events.NotificationEvent;
import com.kamylo.Scrtly_backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        notificationService.createOrUpdateNotification(
                event.getRecipientId(),
                event.getPostId(),
                event.getNotificationType(),
                event.getTriggeringUserName()
        );
    }
}