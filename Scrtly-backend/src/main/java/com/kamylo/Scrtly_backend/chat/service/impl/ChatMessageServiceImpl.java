package com.kamylo.Scrtly_backend.chat.service.impl;

import com.kamylo.Scrtly_backend.chat.web.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.chat.web.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.chat.web.dto.request.ChatMessageEditRequest;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.chat.domain.ChatMessageEntity;
import com.kamylo.Scrtly_backend.chat.domain.ChatRoomEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.chat.repository.ChatMessageRepository;
import com.kamylo.Scrtly_backend.chat.web.dto.request.SendMessageRequest;
import com.kamylo.Scrtly_backend.chat.service.ChatMessageService;
import com.kamylo.Scrtly_backend.chat.service.ChatService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

    @Value("${chat.exchange}")
    private String chatExchangeName;

    @Value("${chat.routing-key-prefix}")
    private String chatRoutingKeyPrefix;

    @Override
    @Async("chatExecutor")
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
        publishAfterCommit(dto);
        return CompletableFuture.completedFuture(dto);
    }

    @Override
    @Async("chatExecutor")
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
            publishAfterCommit(dto);
            return CompletableFuture.completedFuture(dto);
        } else {
            throw new CustomException(BusinessErrorCodes.CHAT_MESSAGE_MISMATCH);
        }
    }

    @Override
    @Async("chatExecutor")
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
            publishAfterCommit(dto);
            return CompletableFuture.completedFuture(dto);
        } else {
            throw new CustomException(BusinessErrorCodes.CHAT_MESSAGE_MISMATCH);
        }
    }

    @Override
    public Page<ChatMessageDto> getChatMessages(Integer chatId, Pageable pageable) {
        return chatMessageRepository.findByChatRoomIdOrderByCreateDateDesc(chatId, pageable)
                .map(chatMessageMapper::mapTo);
    }

    private boolean validateChatMessageOwnership(String username, ChatMessageEntity chatMessage) {
        UserEntity user = userService.findUserByEmail(username);
        return user.getId().equals(chatMessage.getUser().getId());
    }

    private void publishAfterCommit(ChatMessageDto dto) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String routingKey = chatRoutingKeyPrefix + dto.getChatRoomId();
                            rabbitTemplate.convertAndSend(
                                    chatExchangeName,
                                    routingKey,
                                    dto
                            );
                        }
                    }
            );
        }
    }

}
