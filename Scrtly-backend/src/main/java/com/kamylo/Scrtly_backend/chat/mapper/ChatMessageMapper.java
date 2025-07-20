package com.kamylo.Scrtly_backend.chat.mapper;

import com.kamylo.Scrtly_backend.chat.domain.ChatMessageEntity;
import com.kamylo.Scrtly_backend.chat.web.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        uses = UserMapper.class
)
public interface ChatMessageMapper {

    @Mapping(target = "chatRoomId", expression = "java(entity.getChatRoom().getId())")
    @Mapping(target = "status", constant = "sent")
    ChatMessageDto toDto(ChatMessageEntity entity);

    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    ChatMessageEntity toEntity(ChatMessageDto dto);
}
