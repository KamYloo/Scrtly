package com.kamylo.Scrtly_backend.chat.service.impl;

import com.kamylo.Scrtly_backend.chat.mapper.ChatRoomMapper;
import com.kamylo.Scrtly_backend.chat.web.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.chat.web.dto.request.ChatRoomRequest;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.chat.domain.ChatRoomEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.chat.repository.ChatRepository;
import com.kamylo.Scrtly_backend.chat.service.ChatService;
import com.kamylo.Scrtly_backend.user.service.UserService;
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
    private final ChatRoomMapper chatRoomMapper;

    @Override
    public ChatRoomDto createChat(String username, ChatRoomRequest chatRoomRequest) {
        UserEntity reqUser = userService.findUserByEmail(username);
        Set<UserEntity> users = chatRoomRequest.getUserIds().stream()
                .map(userService::findUserById)
                .collect(Collectors.toSet());
        users.add(reqUser);

        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatRoomName(chatRoomRequest.getChatRoomName())
                .participants(List.copyOf(users))
                .build();

        ChatRoomEntity createdChatRoom = chatRepository.save(chatRoom);
        return chatRoomMapper.toDto(createdChatRoom);
    }

    @Override
    public ChatRoomDto getChatById(Integer chatId, String username) {
        ChatRoomEntity chatRoom = chatRepository.findById(chatId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.CHATROOM_NOT_FOUND));
        if (validateChatRoomOwnership(username, chatRoom)) {
            return chatRoomMapper.toDto(chatRoom);
        } else {
            throw new CustomException(BusinessErrorCodes.CHATROOM_MISMATCH);
        }
    }

    @Override
    public List<ChatRoomDto> findChatsByUser(String username) {
        UserEntity reqUser = userService.findUserByEmail(username);
        return chatRepository.findChatRoomsByUserId(reqUser.getId()).stream().map(chatRoomMapper::toDto).toList();
    }

    @Override
    public void deleteChat(Integer chatId, String username) {
        ChatRoomEntity chatRoom = chatRoomMapper.toEntity(getChatById(chatId, username));
        chatRepository.delete(chatRoom);
    }

    private boolean validateChatRoomOwnership(String username, ChatRoomEntity chatRoom) {
        UserEntity user = userService.findUserByEmail(username);
        return chatRoom.getParticipants().stream().anyMatch(participant -> participant.getId().equals(user.getId()));
    }
}
