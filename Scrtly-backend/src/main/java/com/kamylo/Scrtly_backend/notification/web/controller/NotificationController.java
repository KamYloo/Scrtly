package com.kamylo.Scrtly_backend.notification.web.controller;

import com.kamylo.Scrtly_backend.notification.web.dto.NotificationDto;
import com.kamylo.Scrtly_backend.common.response.PagedResponse;
import com.kamylo.Scrtly_backend.notification.service.NotificationService;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Validated
@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/own")
    public ResponseEntity<PagedResponse<NotificationDto>> getOwnerNotifications(@PageableDefault(size = 10)
                                                                                Pageable pageable,
                                                                                Principal principal) {
        Page<NotificationDto> notifications = notificationService.getOwnerNotifications(principal.getName(), pageable);
        return new ResponseEntity<>(PagedResponse.of(notifications), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable @Positive(message = "{id.positive}") Long notificationId, Principal principal)  {
        notificationService.deleteNotification(notificationId, principal.getName());
        return ResponseEntity.ok(notificationId);
    }
}
