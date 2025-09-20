package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.notification.service.NotificationService;
import com.kamylo.Scrtly_backend.notification.web.controller.NotificationController;
import com.kamylo.Scrtly_backend.notification.web.dto.NotificationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController controller;

    @Mock
    private Principal principal;

    private NotificationDto sampleNotification(Long id, String msg) {
        return NotificationDto.builder()
                .id(id)
                .message(msg)
                .seen(false)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .count(1)
                .recipient(null)
                .post(null)
                .build();
    }

    @Test
    void getOwnerNotifications_passesPageable_andReturnsOk() {
        when(principal.getName()).thenReturn("user1");
        NotificationDto dto = sampleNotification(1L, "hello");
        when(notificationService.getOwnerNotifications(eq("user1"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        Pageable pageable = PageRequest.of(0, 10);
        var resp = controller.getOwnerNotifications(pageable, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(notificationService).getOwnerNotifications(eq("user1"), captor.capture());
        Pageable p = captor.getValue();
        assertEquals(0, p.getPageNumber());
        assertEquals(10, p.getPageSize());
    }

    @Test
    void deleteNotification_callsService_andReturnsOkWithId() {
        when(principal.getName()).thenReturn("user2");
        Long id = 42L;

        var resp = controller.deleteNotification(id, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(id, resp.getBody());
        verify(notificationService, times(1)).deleteNotification(id, "user2");
    }
}