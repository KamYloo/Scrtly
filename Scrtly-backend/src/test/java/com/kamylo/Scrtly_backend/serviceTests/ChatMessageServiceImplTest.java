package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.dto.request.ChatMessageEditRequest;
import com.kamylo.Scrtly_backend.entity.ChatMessageEntity;
import com.kamylo.Scrtly_backend.entity.ChatRoomEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.ChatMessageMapperImpl;
import com.kamylo.Scrtly_backend.mappers.ChatRoomMapperImpl;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.ChatMessageRepository;
import com.kamylo.Scrtly_backend.dto.request.SendMessageRequest;
import com.kamylo.Scrtly_backend.service.ChatService;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.service.impl.ChatMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ChatMessageServiceImplTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private UserService userService;

    @Mock
    private ChatService chatService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mapper<ChatRoomEntity, ChatRoomDto> chatRoomMapper = new ChatRoomMapperImpl(new ModelMapper());
        Mapper<com.kamylo.Scrtly_backend.entity.ChatMessageEntity, ChatMessageDto> chatMessageMapper = new ChatMessageMapperImpl(new ModelMapper());
        chatMessageService = new ChatMessageServiceImpl(
                chatMessageRepository,
                userService,
                chatService,
                chatRoomMapper,
                chatMessageMapper,
                rabbitTemplate
        );
    }

    @Test
    void sendMessage_shouldSendMessageSuccessfully() {
        String username = "test@example.com";
        SendMessageRequest request = new SendMessageRequest();
        request.setChatId(1);
        request.setMessage("Hello World");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(100L);
        userEntity.setEmail(username);

        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.setId(1);
        chatRoomDto.setChatRoomName("Test Chat");

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .id(1)
                .build();

        ChatMessageEntity chatMessageEntity = ChatMessageEntity.builder()
                .chatRoom(chatRoomEntity)
                .user(userEntity)
                .messageText("Hello World")
                .build();

        when(userService.findUserByEmail(username)).thenReturn(userEntity);
        when(chatService.getChatById(request.getChatId(), username)).thenReturn(chatRoomDto);
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenReturn(chatMessageEntity);

        CompletableFuture<ChatMessageDto> futureResult = chatMessageService.sendMessageAsync(request, username);
        ChatMessageDto result = futureResult.join();

        assertNotNull(result, "Result should not be null");
        assertEquals("Hello World", result.getMessageText());
        verify(userService, times(1)).findUserByEmail(username);
        verify(chatService, times(1)).getChatById(request.getChatId(), username);
        verify(chatMessageRepository, times(1)).save(any(ChatMessageEntity.class));
    }

    @Test
    void editChatMessage_shouldThrowException_whenChatIdMismatch() {
        ChatMessageEditRequest request = new ChatMessageEditRequest();
        request.setId(1L);
        request.setMessage("Updated message");
        Integer providedChatId = 2;
        String username = "user@example.com";

        UserEntity userEntity = new UserEntity();
        userEntity.setId(100L);
        userEntity.setEmail(username);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .id(1)
                .build();

        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .chatRoom(chatRoomEntity)
                .user(userEntity)
                .messageText("Original message")
                .build();

        when(chatMessageRepository.findById(request.getId())).thenReturn(Optional.of(messageEntity));

        CustomException exception = assertThrows(CustomException.class, () ->
                chatMessageService.editMessageAsync(request, providedChatId, username).join());
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_NOT_IN_CHAT, exception.getErrorCode());
    }

    @Test
    void editMessage_shouldEditSuccessfully_whenChatIdMatchesAndUserIsOwner() {
        ChatMessageEditRequest request = new ChatMessageEditRequest();
        request.setId(1L);
        request.setMessage("Updated message");
        Integer chatId = 1;
        String username = "owner@example.com";

        UserEntity userEntity = new UserEntity();
        userEntity.setId(100L);
        userEntity.setEmail(username);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .id(chatId)
                .build();

        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .chatRoom(chatRoomEntity)
                .user(userEntity)
                .messageText("Original message")
                .build();

        when(chatMessageRepository.findById(request.getId())).thenReturn(Optional.of(messageEntity));
        when(userService.findUserByEmail(username)).thenReturn(userEntity);
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompletableFuture<ChatMessageDto> future = chatMessageService.editMessageAsync(request, chatId, username);
        ChatMessageDto result = future.join();

        assertNotNull(result);
        assertEquals("Updated message", result.getMessageText());
        assertEquals("EDITED", result.getStatus());
        assertEquals(chatId, result.getChatRoomId());
    }

    @Test
    void editMessage_shouldThrowException_whenUserNotOwner() {
        ChatMessageEditRequest request = new ChatMessageEditRequest();
        request.setId(1L);
        request.setMessage("Updated message");
        Integer chatId = 1;
        String username = "user@example.com";

        UserEntity owner = new UserEntity();
        owner.setId(200L);

        UserEntity currentUser = new UserEntity();
        currentUser.setId(100L);
        currentUser.setEmail(username);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .id(chatId)
                .build();

        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .chatRoom(chatRoomEntity)
                .user(owner)
                .messageText("Original message")
                .build();

        when(chatMessageRepository.findById(request.getId())).thenReturn(Optional.of(messageEntity));
        when(userService.findUserByEmail(username)).thenReturn(currentUser);

        CustomException exception = assertThrows(CustomException.class, () ->
                chatMessageService.editMessageAsync(request, chatId, username).join());
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_MISMATCH, exception.getErrorCode());
    }


    @Test
    void getChatMessages_shouldReturnMessages() {
        int chatId = 1;

        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .messageText("Test Message")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<ChatMessageEntity> pageEntity = new PageImpl<>(Collections.singletonList(messageEntity), pageable, 1);

        when(chatMessageRepository.findByChatRoomIdOrderByCreateDateDesc(chatId, pageable)).thenReturn(pageEntity);

        Page<ChatMessageDto> resultPage = chatMessageService.getChatMessages(chatId, pageable);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals("Test Message", resultPage.getContent().get(0).getMessageText());
        verify(chatMessageRepository, times(1)).findByChatRoomIdOrderByCreateDateDesc(chatId, pageable);
    }

    @Test
    void editMessage_shouldThrowException_whenMessageNotFound() {
        ChatMessageEditRequest request = new ChatMessageEditRequest();
        request.setId(99L);
        request.setMessage("Updated message");
        Integer chatId = 1;
        String username = "user@example.com";

        when(chatMessageRepository.findById(request.getId())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                chatMessageService.editMessageAsync(request, chatId, username).join());
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    void deleteChatMessage_shouldDeleteMessage_whenUserIsOwner() {
        String username = "owner@example.com";
        Long messageId = 1L;
        Integer chatId = 1;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(100L);
        userEntity.setEmail(username);

        UserEntity messageOwner = new UserEntity();
        messageOwner.setId(100L);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .id(chatId)
                .build();

        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .user(messageOwner)
                .chatRoom(chatRoomEntity)
                .messageText("Test Message")
                .build();

        when(chatMessageRepository.findById(messageId)).thenReturn(Optional.of(messageEntity));
        when(userService.findUserByEmail(username)).thenReturn(userEntity);

        CompletableFuture<ChatMessageDto> futureResult = chatMessageService.deleteMessageAsync(messageId, chatId, username);
        ChatMessageDto result = futureResult.join();

        verify(chatMessageRepository, times(1)).deleteById(messageId);
        assertEquals("DELETED", result.getStatus());
        assertEquals(chatId, result.getChatRoomId());
    }

    @Test
    void deleteChatMessage_shouldThrowException_whenMessageNotFound() {
        String username = "user@example.com";
        Long messageId = 1L;
        Integer chatId = 1;

        when(chatMessageRepository.findById(messageId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                chatMessageService.deleteMessageAsync(messageId, chatId, username).join());
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_NOT_FOUND, exception.getErrorCode());
        verify(chatMessageRepository, never()).deleteById(any());
    }

    @Test
    void deleteChatMessage_shouldThrowException_whenUserNotOwner() {
        String username = "user@example.com";
        Long messageId = 1L;
        Integer chatId = 1;

        UserEntity currentUser = new UserEntity();
        currentUser.setId(100L);
        currentUser.setEmail(username);

        UserEntity messageOwner = new UserEntity();
        messageOwner.setId(200L);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .id(chatId)
                .build();

        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .user(messageOwner)
                .chatRoom(chatRoomEntity)
                .messageText("Test Message")
                .build();

        when(chatMessageRepository.findById(messageId)).thenReturn(Optional.of(messageEntity));
        when(userService.findUserByEmail(username)).thenReturn(currentUser);

        CustomException exception = assertThrows(CustomException.class, () ->
                chatMessageService.deleteMessageAsync(messageId, chatId, username).join());
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_MISMATCH, exception.getErrorCode());
        verify(chatMessageRepository, never()).deleteById(messageId);
    }

    @Test
    void deleteChatMessage_shouldThrowException_whenChatIdMismatch() {
        Long messageId = 1L;
        Integer providedChatId = 2;
        String username = "user@example.com";

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .id(1)
                .build();

        UserEntity userEntity = new UserEntity();
        userEntity.setId(100L);
        userEntity.setEmail(username);

        ChatMessageEntity chatMessageEntity = ChatMessageEntity.builder()
                .chatRoom(chatRoomEntity)
                .user(userEntity)
                .messageText("Test message")
                .build();

        when(chatMessageRepository.findById(messageId)).thenReturn(Optional.of(chatMessageEntity));

        CustomException exception = assertThrows(CustomException.class, () ->
                chatMessageService.deleteMessageAsync(messageId, providedChatId, username).join());
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_NOT_IN_CHAT, exception.getErrorCode());
    }

}
