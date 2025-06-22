package com.kamylo.Scrtly_backend.notification.web.dto;

import com.kamylo.Scrtly_backend.post.web.dto.PostMinimalDto;
import com.kamylo.Scrtly_backend.user.web.dto.UserMinimalDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private String message;
    private boolean seen;
    private LocalDateTime updatedDate;
    private LocalDateTime createdDate;
    private Integer count;
    private UserMinimalDto recipient;
    private PostMinimalDto post;
}
