package com.kamylo.Scrtly_backend.chat.mapper;

import com.kamylo.Scrtly_backend.chat.web.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.chat.domain.ChatMessageEntity;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ChatMessageMapperImpl implements Mapper<ChatMessageEntity, ChatMessageDto> {

    private ModelMapper modelMapper;

    @Override
    public ChatMessageDto mapTo(ChatMessageEntity chatMessageEntity) {
        return modelMapper.map(chatMessageEntity, ChatMessageDto.class);
    }

    @Override
    public ChatMessageEntity mapFrom(ChatMessageDto chatMessageDto) {
        return modelMapper.map(chatMessageDto, ChatMessageEntity.class);
    }
}
