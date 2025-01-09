package com.kamylo.Scrtly_backend.mappers;

import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.entity.ChatMessageEntity;
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
