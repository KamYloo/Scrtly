package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.NotificationDto;
import com.kamylo.Scrtly_backend.entity.NotificationEntity;
import com.kamylo.Scrtly_backend.entity.PostEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.entity.enums.NotificationType;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.NotificationRepository;
import com.kamylo.Scrtly_backend.repository.PostRepository;
import com.kamylo.Scrtly_backend.service.NotificationService;
import com.kamylo.Scrtly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final PostRepository postRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final Mapper<NotificationEntity, NotificationDto> notificationMapper;

    @Transactional
    @Override
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

    @Override
    public void decrementNotification(Long recipientId, Long postId, NotificationType type) {
        UserEntity recipient = userService.findUserById(recipientId);
        PostEntity post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.POST_NOT_FOUND));
        Optional<NotificationEntity> optionalNotification = notificationRepository.findByRecipientAndTypeAndPost(recipient, type, post);
        if (optionalNotification.isPresent()) {
            NotificationEntity notification = optionalNotification.get();
            if (notification.getCount() > 1) {
                int newCount = notification.getCount() - 1;
                notification.setCount(newCount);
                String senderFullName = notification.getMessage().split(" ")[0];
                notification.setMessage(buildNotificationMessage(senderFullName, type, newCount));
                notification.setUpdatedDate(LocalDateTime.now());
                notificationRepository.save(notification);
            } else {
                notificationRepository.delete(notification);
            }
        }
    }

    @Override
    public void deleteNotificationsByPost(PostEntity post) {
        notificationRepository.deleteAllByPost(post);
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
        NotificationDto notificationDto = notificationMapper.mapTo(notification);
        messagingTemplate.convertAndSendToUser(
                notification.getRecipient().getUsername(),
                "/queue/notifications",
                notificationDto
        );
    }

    @Override
    public Page<NotificationDto> getOwnerNotifications(String username, Pageable pageable) {
        UserEntity user = userService.findUserByEmail(username);
        return notificationRepository.findByRecipientIdOrderByUpdatedDateDesc(user.getId(), pageable).map(notificationMapper::mapTo);
    }

    @Override
    public void deleteNotification(Long id, String username) {
        NotificationEntity notification = notificationRepository.findById(id).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NOTIFICATION_NOT_FOUND));
        UserEntity userEntity = userService.findUserByEmail(username);
        if (notification.getRecipient().getId().equals(userEntity.getId())) {
            notificationRepository.deleteById(notification.getId());
        } else {
            throw new CustomException(BusinessErrorCodes.NOTIFICATION_MISMATCH);
        }
    }

}
