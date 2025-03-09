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

            notification.setCount(notification.getCount() + 1);
            if(type == NotificationType.LIKE) {
                notification.setMessage(sender.getFullName() + " i " + (notification.getCount() - 1) + " innych polubiło Twoje zdjęcie");
            } else if(type == NotificationType.COMMENT) {
                notification.setMessage(sender.getFullName() + " i " + (notification.getCount() - 1) + " innych skomentowało Twoje zdjęcie");
            }
            notification.setCreatedDate(LocalDateTime.now());
        } else {

            String message;
            if(type == NotificationType.LIKE) {
                message = sender.getFullName() + " polubił Twoje zdjęcie";
            } else {
                message = sender.getFullName() + " skomentował Twoje zdjęcie";
            }
            notification = NotificationEntity.builder()
                    .recipient(recipient)
                    .post(post)
                    .type(type)
                    .count(1)
                    .message(message)
                    .seen(false)
                    .build();
        }
        NotificationEntity savedNotification = notificationRepository.save(notification);
        sendNotification(savedNotification);
    }

    private void sendNotification(NotificationEntity notification) {
        messagingTemplate.convertAndSendToUser(
                notification.getRecipient().getUsername(),
                "/queue/notifications",
                notification
        );
    }
}
