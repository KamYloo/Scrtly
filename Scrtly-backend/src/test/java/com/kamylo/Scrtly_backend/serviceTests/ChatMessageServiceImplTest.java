package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.entity.ChatMessageEntity;
import com.kamylo.Scrtly_backend.entity.ChatRoomEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.ChatMessageMapperImpl;
import com.kamylo.Scrtly_backend.mappers.ChatRoomMapperImpl;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.ChatMessageRepository;
import com.kamylo.Scrtly_backend.request.SendMessageRequest;
import com.kamylo.Scrtly_backend.service.ChatService;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.service.impl.ChatMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mapper<ChatRoomEntity, ChatRoomDto> chatRoomMapper = new ChatRoomMapperImpl(new ModelMapper());
        Mapper<ChatMessageEntity, ChatMessageDto> chatMessageMapper = new ChatMessageMapperImpl(new ModelMapper());
        chatMessageService = new ChatMessageServiceImpl(
                chatMessageRepository, userService, chatService, chatRoomMapper, chatMessageMapper
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
        chatRoomDto.setChatRoomName("Test Chat");

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .id(1)
                .build();

        ChatMessageEntity chatMessageEntity = ChatMessageEntity.builder()
                .chatRoom(chatRoomEntity)
                .user(userEntity)
                .messageText("Hello World")
                .build();

        ChatMessageDto expectedDto = new ChatMessageDto();
        expectedDto.setMessageText("Hello World");

        when(userService.findUserByEmail(username)).thenReturn(userEntity);
        when(chatService.getChatById(request.getChatId(), username)).thenReturn(chatRoomDto);
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenReturn(chatMessageEntity);

        ChatMessageDto result = chatMessageService.sendMessage(request, username);

        assertNotNull(result, "Result should not be null");
        assertEquals("Hello World", result.getMessageText());
        verify(userService, times(1)).findUserByEmail(username);
        verify(chatService, times(1)).getChatById(request.getChatId(), username);
        verify(chatMessageRepository, times(1)).save(any(ChatMessageEntity.class));
    }

    @Test
    void getChatsMessages_shouldReturnMessages() {
        String username = "user@example.com";
        int chatId = 1;

        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.setId(chatId); // FIXED: Set the ID
        chatRoomDto.setChatRoomName("Test Chat");

        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .messageText("Test Message")
                .build();

        ChatMessageDto messageDto = new ChatMessageDto();
        messageDto.setMessageText("Test Message");

        when(chatService.getChatById(chatId, username)).thenReturn(chatRoomDto);
        when(chatMessageRepository.findByChatRoomId(chatId))
                .thenReturn(Collections.singletonList(messageEntity));

        List<ChatMessageDto> result = chatMessageService.getChatsMessages(chatId, username);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Message", result.get(0).getMessageText());
        verify(chatService, times(1)).getChatById(chatId, username);
        verify(chatMessageRepository, times(1)).findByChatRoomId(chatId);
    }


    @Test
    void deleteChatMessage_shouldDeleteMessage_whenUserIsOwner() {
        String username = "owner@example.com";
        int messageId = 1;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(100L);
        userEntity.setEmail(username);

        UserEntity messageOwner = new UserEntity();
        messageOwner.setId(100L);

        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .user(messageOwner)
                .messageText("Test Message")
                .build();

        when(chatMessageRepository.findById(messageId)).thenReturn(Optional.of(messageEntity));
        when(userService.findUserByEmail(username)).thenReturn(userEntity);

        chatMessageService.deleteChatMessage(messageId, username);

        verify(chatMessageRepository, times(1)).deleteById(messageId);
    }

    @Test
    void deleteChatMessage_shouldThrowException_whenMessageNotFound() {
        String username = "user@example.com";
        int messageId = 1;

        when(chatMessageRepository.findById(messageId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                chatMessageService.deleteChatMessage(messageId, username));
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_NOT_FOUND, exception.getErrorCode());
        verify(chatMessageRepository, never()).deleteById(any());
    }

    @Test
    void deleteChatMessage_shouldThrowException_whenUserNotOwner() {
        String username = "user@example.com";
        int messageId = 1;

        UserEntity currentUser = new UserEntity();
        currentUser.setId(100L);
        currentUser.setEmail(username);

        UserEntity messageOwner = new UserEntity();
        messageOwner.setId(200L);

        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .user(messageOwner)
                .messageText("Test Message")
                .build();

        when(chatMessageRepository.findById(messageId)).thenReturn(Optional.of(messageEntity));
        when(userService.findUserByEmail(username)).thenReturn(currentUser);

        CustomException exception = assertThrows(CustomException.class, () ->
                chatMessageService.deleteChatMessage(messageId, username));
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_MISMATCH, exception.getErrorCode());
        verify(chatMessageRepository, never()).deleteById(messageId);
    }
}