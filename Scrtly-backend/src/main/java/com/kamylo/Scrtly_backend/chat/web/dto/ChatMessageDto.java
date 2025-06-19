package com.kamylo.Scrtly_backend.chat.web.dto;

import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private Long id;
    private String messageText;
    private LocalDateTime createDate;
    private LocalDateTime lastModifiedDate;
    private UserDto user;
    private Integer chatRoomId;
    private String status;
}
