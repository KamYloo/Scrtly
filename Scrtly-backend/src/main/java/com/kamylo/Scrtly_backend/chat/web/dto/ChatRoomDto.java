package com.kamylo.Scrtly_backend.chat.web.dto;

import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto {
    private Integer id;
    private String chatRoomName;
    private List<UserDto> participants;
}
