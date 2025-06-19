package com.kamylo.Scrtly_backend.chat.mapper;

import com.kamylo.Scrtly_backend.chat.web.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.chat.domain.ChatRoomEntity;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ChatRoomMapperImpl implements Mapper<ChatRoomEntity, ChatRoomDto> {

    private ModelMapper modelMapper;

    @Override
    public ChatRoomDto mapTo(ChatRoomEntity chatRoomEntity) {
        return modelMapper.map(chatRoomEntity, ChatRoomDto.class);
    }

    @Override
    public ChatRoomEntity mapFrom(ChatRoomDto chatRoomDto) {
        return modelMapper.map(chatRoomDto, ChatRoomEntity.class);
    }
}
