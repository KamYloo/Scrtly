package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.NotificationDto;
import com.kamylo.Scrtly_backend.response.PagedResponse;
import com.kamylo.Scrtly_backend.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/own")
    public ResponseEntity<PagedResponse<NotificationDto>> getOwnerNotifications(@PageableDefault(size = 10, sort = "updatedDate", direction = Sort.Direction.DESC)
                                                                                Pageable pageable,
                                                                                Principal principal) {
        Page<NotificationDto> notifications = notificationService.getOwnerNotifications(principal.getName(), pageable);
        return new ResponseEntity<>(PagedResponse.of(notifications), HttpStatus.OK);
    }
}
