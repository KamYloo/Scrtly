package com.kamylo.Scrtly_backend.dto;

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
    private Integer id;
    private String messageText;
    private LocalDateTime timestamp;
    private UserDto user;
    private ChatRoomDto chatRoom;
}
