package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.config.RabbitMQConfig;
import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.dto.request.ChatMessageEditRequest;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.entity.ChatMessageEntity;
import com.kamylo.Scrtly_backend.entity.ChatRoomEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.ChatMessageRepository;
import com.kamylo.Scrtly_backend.dto.request.SendMessageRequest;
import com.kamylo.Scrtly_backend.service.ChatMessageService;
import com.kamylo.Scrtly_backend.service.ChatService;
import com.kamylo.Scrtly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final ChatService chatService;
    private final Mapper<ChatRoomEntity, ChatRoomDto> chatRoomMapper;
    private final Mapper<ChatMessageEntity, ChatMessageDto> chatMessageMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Async
    @Transactional
    public CompletableFuture<ChatMessageDto> sendMessageAsync(SendMessageRequest request, String username) {
        UserEntity userEntity = userService.findUserByEmail(username);
        ChatRoomDto chatRoomDto = chatService.getChatById(request.getChatId(), username);
        ChatRoomEntity chatRoom = chatRoomMapper.mapFrom(chatRoomDto);
        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .user(userEntity)
                .messageText(request.getMessage())
                .build();

        ChatMessageEntity savedChatMessage = chatMessageRepository.save(chatMessage);
        ChatMessageDto dto = chatMessageMapper.mapTo(savedChatMessage);
        dto.setChatRoomId(chatRoom.getId());
        dto.setStatus("NEW");

        rabbitTemplate.convertAndSend(RabbitMQConfig.CHAT_EXCHANGE, RabbitMQConfig.ROUTING_KEY, dto);
        return CompletableFuture.completedFuture(dto);
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<ChatMessageDto> editMessageAsync(ChatMessageEditRequest request, Integer chatId, String username) {
        ChatMessageEntity message = chatMessageRepository.findById(request.getId()).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.CHAT_MESSAGE_NOT_FOUND));

        if (!message.getChatRoom().getId().equals(chatId)) {
            throw new CustomException(BusinessErrorCodes.CHAT_MESSAGE_NOT_IN_CHAT);
        }

        if (validateChatMessageOwnership(username, message)) {
            message.setMessageText(request.getMessage());
            message.setLastModifiedDate(LocalDateTime.now());
            ChatMessageEntity updatedMessage = chatMessageRepository.save(message);
            ChatMessageDto dto = chatMessageMapper.mapTo(updatedMessage);
            dto.setChatRoomId(message.getChatRoom().getId());
            dto.setStatus("EDITED");
            rabbitTemplate.convertAndSend(RabbitMQConfig.CHAT_EXCHANGE, RabbitMQConfig.ROUTING_KEY, dto);
            return CompletableFuture.completedFuture(dto);
        } else {
            throw new CustomException(BusinessErrorCodes.CHAT_MESSAGE_MISMATCH);
        }
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<ChatMessageDto> deleteMessageAsync(Long messageId, Integer chatId, String username) {
        ChatMessageEntity chatMessage = chatMessageRepository.findById(messageId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.CHAT_MESSAGE_NOT_FOUND));
        if (!chatMessage.getChatRoom().getId().equals(chatId)) {
            throw new CustomException(BusinessErrorCodes.CHAT_MESSAGE_NOT_IN_CHAT);
        }
        if (validateChatMessageOwnership(username, chatMessage)) {
            chatMessageRepository.deleteById(messageId);
            ChatMessageDto dto = ChatMessageDto.builder()
                    .id(chatMessage.getId())
                    .messageText("message deleted")
                    .chatRoomId(chatMessage.getChatRoom().getId())
                    .status("DELETED")
                    .build();
            rabbitTemplate.convertAndSend(RabbitMQConfig.CHAT_EXCHANGE, RabbitMQConfig.ROUTING_KEY, dto);
            return CompletableFuture.completedFuture(dto);
        } else {
            throw new CustomException(BusinessErrorCodes.CHAT_MESSAGE_MISMATCH);
        }
    }

    @Override
    public Page<ChatMessageDto> getChatMessages(Integer chatId, String username, Pageable pageable) {
        return chatMessageRepository.findByChatRoomIdOrderByCreateDateDesc(chatId, pageable).map(chatMessageMapper::mapTo);
    }


    private boolean validateChatMessageOwnership(String username, ChatMessageEntity chatMessage) {
        UserEntity user = userService.findUserByEmail(username);
        return user.getId().equals(chatMessage.getUser().getId());
    }
}
