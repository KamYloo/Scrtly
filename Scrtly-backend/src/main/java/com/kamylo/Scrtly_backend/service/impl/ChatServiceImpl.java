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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;
    private final Mapper<ChatRoomEntity, ChatRoomDto> chatRoomMapper;

    @Override
    public ChatRoomDto createChat(String username, List<Long> userIds) {
        UserEntity reqUser = userService.findUserByEmail(username);
        Set<UserEntity> users = userIds.stream()
                .map(userService::findUserById)
                .collect(Collectors.toSet());
        users.add(reqUser);

        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatRoomName("Chat_" + System.currentTimeMillis())
                .participants(List.copyOf(users))
                .build();

        ChatRoomEntity createdChatRoom = chatRepository.save(chatRoom);
        return chatRoomMapper.mapTo(createdChatRoom);
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
        return chatRoom.getParticipants().stream().anyMatch(participant -> participant.getId().equals(user.getId()));
    }
}
