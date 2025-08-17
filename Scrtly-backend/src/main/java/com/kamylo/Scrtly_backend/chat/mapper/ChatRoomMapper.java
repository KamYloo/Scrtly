package com.kamylo.Scrtly_backend.chat.mapper;

import com.kamylo.Scrtly_backend.chat.domain.ChatRoomEntity;
import com.kamylo.Scrtly_backend.chat.web.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        uses = {UserMapper.class}
)
public interface ChatRoomMapper {
    ChatRoomDto toDto(ChatRoomEntity entity);
    ChatRoomEntity toEntity(ChatRoomDto dto);
}
