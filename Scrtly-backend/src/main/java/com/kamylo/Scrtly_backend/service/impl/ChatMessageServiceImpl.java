package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.entity.ChatMessageEntity;
import com.kamylo.Scrtly_backend.entity.ChatRoomEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.ChatMessageRepository;
import com.kamylo.Scrtly_backend.request.SendMessageRequest;
import com.kamylo.Scrtly_backend.service.ChatMessageService;
import com.kamylo.Scrtly_backend.service.ChatService;
import com.kamylo.Scrtly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final ChatService chatService;
    private final Mapper<ChatRoomEntity, ChatRoomDto> chatRoomMapper;
    private final Mapper<ChatMessageEntity, ChatMessageDto> chatMessageMapper;

    @Override
    @Transactional
    public ChatMessageDto sendMessage(SendMessageRequest request, String username) {
        UserEntity userEntity = userService.findUserByEmail(username);
        ChatRoomEntity chatRoomEntity = chatRoomMapper.mapFrom(chatService.getChatById(request.getChatId(), username));

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .chatRoom(chatRoomEntity)
                .user(userEntity)
                .messageText(request.getMessage())
                .build();

        ChatMessageEntity savedChatMessage= chatMessageRepository.save(chatMessage);
        return chatMessageMapper.mapTo(savedChatMessage);
    }

    @Override
    public List<ChatMessageDto> getChatsMessages(Integer chatId, String username) {
        ChatRoomEntity chatRoom = chatRoomMapper.mapFrom(chatService.getChatById(chatId, username));
        return chatMessageRepository.findByChatRoomId(chatRoom.getId()).stream().map(chatMessageMapper::mapTo).toList();
    }

    @Override
    public void deleteChatMessage(Integer messageId, String username) {
        ChatMessageEntity chatMessage = chatMessageRepository.findById(messageId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.CHAT_MESSAGE_NOT_FOUND));
        if (validateChatMessageOwnership(username, chatMessage)) {
            chatMessageRepository.deleteById(messageId);
        } else {
            throw new CustomException(BusinessErrorCodes.CHAT_MESSAGE_MISMATCH);
        }
    }

    private boolean validateChatMessageOwnership(String username, ChatMessageEntity chatMessage) {
        UserEntity user = userService.findUserByEmail(username);
        return user.getId().equals(chatMessage.getUser().getId());
    }
}
