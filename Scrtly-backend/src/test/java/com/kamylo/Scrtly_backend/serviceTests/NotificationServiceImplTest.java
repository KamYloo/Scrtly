package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.notification.mapper.NotificationMapper;
import com.kamylo.Scrtly_backend.notification.service.NotificationServiceImpl;
import com.kamylo.Scrtly_backend.notification.web.dto.NotificationDto;
import com.kamylo.Scrtly_backend.notification.domain.NotificationEntity;
import com.kamylo.Scrtly_backend.notification.domain.enums.NotificationType;
import com.kamylo.Scrtly_backend.notification.repository.NotificationRepository;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.post.repository.PostRepository;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private PostRepository postRepository;
    @Mock @Qualifier("notificationRabbitTemplate") private RabbitTemplate rabbitTemplate;
    @Mock private UserService userService;
    @Mock private NotificationMapper notificationMapper;

    @InjectMocks private NotificationServiceImpl service;

    private UserEntity recipient;
    private UserEntity sender;
    private PostEntity post;

    @BeforeEach
    void setUp() throws Exception {
        recipient = new UserEntity();
        recipient.setId(10L);
        recipient.setFullName("recipientUser");
        sender = new UserEntity();
        sender.setId(11L);
        sender.setFullName("senderUser");
        post = new PostEntity();
        post.setId(100L);

        Field f1 = NotificationServiceImpl.class.getDeclaredField("notifExchangeName");
        f1.setAccessible(true);
        f1.set(service, "exchange-test");
        Field f2 = NotificationServiceImpl.class.getDeclaredField("notifRoutingKey");
        f2.setAccessible(true);
        f2.set(service, "rk-");
    }

    @Test
    void createOrUpdateNotification_ignoresWhenRecipientIsSender() {
        when(userService.findUserById(10L)).thenReturn(recipient);
        when(userService.findUserByEmail("senderUser")).thenReturn(recipient); // same user

        service.createOrUpdateNotification(10L, 100L, NotificationType.LIKE, "senderUser");

        verifyNoInteractions(notificationRepository);
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void createOrUpdateNotification_throwsWhenPostMissing() {
        when(userService.findUserById(10L)).thenReturn(recipient);
        when(userService.findUserByEmail("senderUser")).thenReturn(sender);
        when(postRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class,
                () -> service.createOrUpdateNotification(10L, 100L, NotificationType.COMMENT, "senderUser"));

        verify(notificationRepository, never()).save(any());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void createOrUpdateNotification_updatesExistingAndSends() {
        NotificationEntity existing = NotificationEntity.builder()
                .id(1L)
                .count(2)
                .message("Alice polubił Twoje zdjęcie")
                .recipient(recipient)
                .post(post)
                .type(NotificationType.LIKE)
                .build();

        NotificationDto dto = new NotificationDto();
        dto.setId(1L);

        when(userService.findUserById(10L)).thenReturn(recipient);
        when(userService.findUserByEmail("senderUser")).thenReturn(sender);
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(notificationRepository.findByRecipientAndTypeAndPost(recipient, NotificationType.LIKE, post))
                .thenReturn(Optional.of(existing));
        when(notificationRepository.save(any(NotificationEntity.class))).thenAnswer(i -> i.getArguments()[0]);
        when(notificationMapper.toDto(any(NotificationEntity.class))).thenReturn(dto);

        service.createOrUpdateNotification(10L, 100L, NotificationType.LIKE, "senderUser");

        assertEquals(3, existing.getCount());
        assertNotNull(existing.getUpdatedDate());
        verify(notificationRepository).save(existing);
        verify(rabbitTemplate).convertAndSend(eq("exchange-test"), eq("rk-" + recipient.getUsername()), eq(dto));
    }

    @Test
    void createOrUpdateNotification_createsNewAndSends() {
        when(userService.findUserById(10L)).thenReturn(recipient);
        when(userService.findUserByEmail("senderUser")).thenReturn(sender);
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(notificationRepository.findByRecipientAndTypeAndPost(recipient, NotificationType.COMMENT, post))
                .thenReturn(Optional.empty());
        when(notificationRepository.save(any(NotificationEntity.class))).thenAnswer(inv -> {
            NotificationEntity n = inv.getArgument(0);
            n.setId(5L);
            return n;
        });
        NotificationDto dto = new NotificationDto();
        dto.setId(5L);
        when(notificationMapper.toDto(any(NotificationEntity.class))).thenReturn(dto);

        service.createOrUpdateNotification(10L, 100L, NotificationType.COMMENT, "senderUser");

        ArgumentCaptor<NotificationEntity> capt = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository).save(capt.capture());
        NotificationEntity saved = capt.getValue();
        assertEquals(1, saved.getCount());
        assertFalse(saved.isSeen());
        verify(rabbitTemplate).convertAndSend(eq("exchange-test"), eq("rk-" + recipient.getUsername()), eq(dto));
    }

    @Test
    void decrementNotification_decrementsWhenCountGreaterThanOne() {
        NotificationEntity n = NotificationEntity.builder()
                .id(2L)
                .count(3)
                .message("Alice polubił Twoje zdjęcie")
                .recipient(recipient)
                .post(post)
                .type(NotificationType.LIKE)
                .build();

        when(userService.findUserById(10L)).thenReturn(recipient);
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(notificationRepository.findByRecipientAndTypeAndPost(recipient, NotificationType.LIKE, post))
                .thenReturn(Optional.of(n));

        service.decrementNotification(10L, 100L, NotificationType.LIKE);

        assertEquals(2, n.getCount());
        assertNotNull(n.getUpdatedDate());
        verify(notificationRepository).save(n);
    }

    @Test
    void decrementNotification_deletesWhenCountEqualsOne() {
        NotificationEntity n = NotificationEntity.builder()
                .id(3L)
                .count(1)
                .message("Bob skomentował Twoje zdjęcie")
                .recipient(recipient)
                .post(post)
                .type(NotificationType.COMMENT)
                .build();

        when(userService.findUserById(10L)).thenReturn(recipient);
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(notificationRepository.findByRecipientAndTypeAndPost(recipient, NotificationType.COMMENT, post))
                .thenReturn(Optional.of(n));

        service.decrementNotification(10L, 100L, NotificationType.COMMENT);

        verify(notificationRepository).delete(n);
    }

    @Test
    void deleteNotificationsByPost_delegatesToRepository() {
        service.deleteNotificationsByPost(post);
        verify(notificationRepository).deleteAllByPost(post);
    }

    @Test
    void getOwnerNotifications_mapsPage() {
        NotificationEntity n = NotificationEntity.builder()
                .id(7L)
                .recipient(recipient)
                .message("m")
                .build();
        Page<NotificationEntity> page = new PageImpl<>(List.of(n));
        when(userService.findUserByEmail("u")).thenReturn(sender);
        when(notificationRepository.findByRecipientIdOrderByUpdatedDateDescCreatedDateDesc(sender.getId(), Pageable.unpaged()))
                .thenReturn(page);
        NotificationDto dto = new NotificationDto();
        dto.setId(7L);
        when(notificationMapper.toDto(n)).thenReturn(dto);

        Page<NotificationDto> resp = service.getOwnerNotifications("u", Pageable.unpaged());

        assertEquals(1, resp.getTotalElements());
        assertEquals(dto, resp.getContent().get(0));
    }

    @Test
    void deleteNotification_deletesWhenRecipientMatches() {
        NotificationEntity n = NotificationEntity.builder()
                .id(9L)
                .recipient(recipient)
                .build();
        when(notificationRepository.findById(9L)).thenReturn(Optional.of(n));
        when(userService.findUserByEmail("r")).thenReturn(recipient);

        service.deleteNotification(9L, "r");

        verify(notificationRepository).deleteById(9L);
    }

    @Test
    void deleteNotification_throwsWhenNotFound() {
        when(notificationRepository.findById(1000L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.deleteNotification(1000L, "any"));
    }

    @Test
    void deleteNotification_throwsWhenRecipientMismatch() {
        NotificationEntity n = NotificationEntity.builder()
                .id(20L)
                .recipient(recipient)
                .build();
        UserEntity other = new UserEntity();
        other.setId(999L);

        when(notificationRepository.findById(20L)).thenReturn(Optional.of(n));
        when(userService.findUserByEmail("someone")).thenReturn(other);

        assertThrows(CustomException.class, () -> service.deleteNotification(20L, "someone"));
    }
}