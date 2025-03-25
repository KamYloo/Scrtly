package com.kamylo.Scrtly_backend.listeners;

import com.kamylo.Scrtly_backend.events.NotificationEvent;
import com.kamylo.Scrtly_backend.service.NotificationService;
import com.kamylo.Scrtly_backend.service.impl.NotificationServiceImpl;
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