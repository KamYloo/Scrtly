package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.entity.ChatRoomEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.ChatRepository;
import com.kamylo.Scrtly_backend.service.ChatService;
import com.kamylo.Scrtly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;
    private final Mapper<ChatRoomEntity, ChatRoomDto> chatRoomMapper;

    @Override
    public ChatRoomDto createChat(String username, Long userId) {
        UserEntity reqUser = userService.findUserByEmail(username);
        UserEntity user2 = userService.findUserById(userId);
        ChatRoomEntity isChatExist = chatRepository.findSingleChatRoomById(reqUser, user2);

        if (isChatExist != null) {
            return chatRoomMapper.mapTo(isChatExist);
        } else {
            ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                    .firstPerson(reqUser)
                    .secondPerson(user2)
                    .build();

            ChatRoomEntity createdChatRoom = chatRepository.save(chatRoom);
            return chatRoomMapper.mapTo(createdChatRoom);
        }
    }

    @Override
    public ChatRoomDto getChatById(Integer chatId, String username) {
        ChatRoomEntity chatRoom = chatRepository.findById(chatId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.CHATROOM_NOT_FOUND));
        if (validateChatRoomOwnership(username, chatRoom)) {
            return chatRoomMapper.mapTo(chatRoom);
        } else {
            throw new CustomException(BusinessErrorCodes.CHATROOM_MISMATCH);
        }
    }

    @Override
    public List<ChatRoomDto> findChatsByUser(String username) {
        UserEntity reqUser = userService.findUserByEmail(username);
        return chatRepository.findChatRoomsByUserId(reqUser.getId()).stream().map(chatRoomMapper::mapTo).toList();
    }

    @Override
    public void deleteChat(Integer chatId, String username) {
        ChatRoomEntity chatRoom = chatRoomMapper.mapFrom(getChatById(chatId, username));
        chatRepository.delete(chatRoom);
    }

    private boolean validateChatRoomOwnership(String username, ChatRoomEntity chatRoom) {
        UserEntity user = userService.findUserByEmail(username);
        return (user.getId().equals(chatRoom.getFirstPerson().getId()) || user.getId().equals(chatRoom.getSecondPerson().getId()));
    }
}
