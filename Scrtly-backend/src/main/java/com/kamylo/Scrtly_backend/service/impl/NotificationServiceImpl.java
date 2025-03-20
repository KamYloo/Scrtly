package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.entity.NotificationEntity;
import com.kamylo.Scrtly_backend.entity.PostEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.entity.enums.NotificationType;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.repository.NotificationRepository;
import com.kamylo.Scrtly_backend.repository.PostRepository;
import com.kamylo.Scrtly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl {
    private final NotificationRepository notificationRepository;
    private final PostRepository postRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    @Transactional
    public void createOrUpdateNotification(Long userId, Long postId, NotificationType type, String triggeringUserName) {
        UserEntity recipient = userService.findUserById(userId);
        UserEntity sender = userService.findUserByEmail(triggeringUserName);

        PostEntity post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.POST_NOT_FOUND));

        Optional<NotificationEntity> optionalNotification = notificationRepository.findByRecipientAndTypeAndPost(recipient, type, post);
        NotificationEntity notification;
        if (optionalNotification.isPresent()) {
            notification = optionalNotification.get();
            int newCount = notification.getCount() + 1;
            notification.setCount(newCount);
            notification.setMessage(buildNotificationMessage(sender.getFullName(), type, newCount));
            notification.setUpdatedDate(LocalDateTime.now());
        } else {
            notification = NotificationEntity.builder()
                    .recipient(recipient)
                    .post(post)
                    .type(type)
                    .count(1)
                    .message(buildNotificationMessage(sender.getFullName(), type, 1))
                    .seen(false)
                    .build();
        }

        NotificationEntity savedNotification = notificationRepository.save(notification);
        sendNotification(savedNotification);
    }

    private String buildNotificationMessage(String senderFullName, NotificationType type, int count) {
        if (type == NotificationType.LIKE) {
            if (count == 1) {
                return senderFullName + " polubił Twoje zdjęcie";
            } else if (count == 2) {
                return senderFullName + " i 1 inna osoba polubiły Twoje zdjęcie";
            } else {
                return senderFullName + " i " + (count - 1) + " innych osób polubiły Twoje zdjęcie";
            }
        } else if (type == NotificationType.COMMENT) {
            if (count == 1) {
                return senderFullName + " skomentował Twoje zdjęcie";
            } else if (count == 2) {
                return senderFullName + " i 1 inna osoba skomentowały Twoje zdjęcie";
            } else {
                return senderFullName + " i " + (count - 1) + " innych osób skomentowały Twoje zdjęcie";
            }
        }
        return "";
    }


    private void sendNotification(NotificationEntity notification) {
        messagingTemplate.convertAndSendToUser(
                notification.getRecipient().getUsername(),
                "/queue/notifications",
                notification
        );
    }
}
