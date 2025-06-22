package com.kamylo.Scrtly_backend.notification.events;

import com.kamylo.Scrtly_backend.notification.domain.enums.NotificationType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {
    private final Long recipientId;
    private final Long postId;
    private final NotificationType notificationType;
    private final String triggeringUserName;

    public NotificationEvent(Object source, Long recipientId, Long postId, NotificationType notificationType, String triggeringUserName) {
        super(source);
        this.recipientId = recipientId;
        this.postId = postId;
        this.notificationType = notificationType;
        this.triggeringUserName = triggeringUserName;
    }
}
