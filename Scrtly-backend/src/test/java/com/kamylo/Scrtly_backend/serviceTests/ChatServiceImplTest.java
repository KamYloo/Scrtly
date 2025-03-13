package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.entity.ChatRoomEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.ChatRepository;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ChatServiceImplTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserService userService;

    @Mock
    private Mapper<ChatRoomEntity, ChatRoomDto> chatRoomMapper;

    @InjectMocks
    private ChatServiceImpl chatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createChat_shouldCreateChatWithCorrectParticipants() {
        String username = "test@example.com";
        List<Long> userIds = Arrays.asList(2L, 3L);

        UserEntity requester = new UserEntity();
        requester.setId(1L);
        requester.setEmail(username);

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        UserEntity user3 = new UserEntity();
        user3.setId(3L);

        when(userService.findUserByEmail(username)).thenReturn(requester);
        when(userService.findUserById(2L)).thenReturn(user2);
        when(userService.findUserById(3L)).thenReturn(user3);

        ChatRoomEntity savedChatRoom = ChatRoomEntity.builder()
                // Nazwa czatu generowana na podstawie czasu – w teście ustawiamy przykładową wartość
                .chatRoomName("Chat_123456789")
                .participants(Arrays.asList(requester, user2, user3))
                .build();
        when(chatRepository.save(any(ChatRoomEntity.class))).thenReturn(savedChatRoom);

        ChatRoomDto expectedDto = new ChatRoomDto();
        expectedDto.setChatRoomName(savedChatRoom.getChatRoomName());
        when(chatRoomMapper.mapTo(savedChatRoom)).thenReturn(expectedDto);

        ChatRoomDto result = chatService.createChat(username, userIds);

        assertNotNull(result);
        assertEquals(expectedDto.getChatRoomName(), result.getChatRoomName());
        verify(userService, times(1)).findUserByEmail(username);
        verify(userService, times(1)).findUserById(2L);
        verify(userService, times(1)).findUserById(3L);
        verify(chatRepository, times(1)).save(any(ChatRoomEntity.class));
        verify(chatRoomMapper, times(1)).mapTo(savedChatRoom);
    }

    @Test
    void getChatById_shouldReturnChat_whenUserIsParticipant() {
        String username = "participant@example.com";
        int chatId = 1;

        UserEntity user = new UserEntity();
        user.setId(10L);
        user.setEmail(username);

        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatRoomName("Chat_123")
                .participants(Collections.singletonList(user))
                .build();

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chatRoom));
        when(userService.findUserByEmail(username)).thenReturn(user);

        ChatRoomDto expectedDto = new ChatRoomDto();
        expectedDto.setChatRoomName(chatRoom.getChatRoomName());
        when(chatRoomMapper.mapTo(chatRoom)).thenReturn(expectedDto);

        ChatRoomDto result = chatService.getChatById(chatId, username);

        assertNotNull(result);
        assertEquals(expectedDto.getChatRoomName(), result.getChatRoomName());
        verify(chatRepository, times(1)).findById(chatId);
        verify(userService, times(1)).findUserByEmail(username);
        verify(chatRoomMapper, times(1)).mapTo(chatRoom);
    }

    @Test
    void getChatById_shouldThrowException_whenChatNotFound() {
        String username = "user@example.com";
        int chatId = 1;

        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            chatService.getChatById(chatId, username);
        });
        assertEquals(BusinessErrorCodes.CHATROOM_NOT_FOUND, exception.getErrorCode());
        verify(chatRepository, times(1)).findById(chatId);
        verify(userService, never()).findUserByEmail(any());
    }

    @Test
    void getChatById_shouldThrowException_whenUserNotParticipant() {
        String username = "nonparticipant@example.com";
        int chatId = 1;

        UserEntity user = new UserEntity();
        user.setId(10L);
        user.setEmail(username);

        UserEntity otherUser = new UserEntity();
        otherUser.setId(20L);
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatRoomName("Chat_123")
                .participants(Collections.singletonList(otherUser))
                .build();

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chatRoom));
        when(userService.findUserByEmail(username)).thenReturn(user);

        CustomException exception = assertThrows(CustomException.class, () -> {
            chatService.getChatById(chatId, username);
        });
        assertEquals(BusinessErrorCodes.CHATROOM_MISMATCH, exception.getErrorCode());
        verify(chatRepository, times(1)).findById(chatId);
        verify(userService, times(1)).findUserByEmail(username);
    }

    @Test
    void findChatsByUser_shouldReturnChatsForUser() {
        String username = "user@example.com";

        UserEntity user = new UserEntity();
        user.setId(5L);
        user.setEmail(username);

        ChatRoomEntity chatRoom1 = ChatRoomEntity.builder()
                .chatRoomName("Chat_1")
                .participants(Collections.singletonList(user))
                .build();
        ChatRoomEntity chatRoom2 = ChatRoomEntity.builder()
                .chatRoomName("Chat_2")
                .participants(Collections.singletonList(user))
                .build();

        List<ChatRoomEntity> chatRooms = Arrays.asList(chatRoom1, chatRoom2);

        when(userService.findUserByEmail(username)).thenReturn(user);
        when(chatRepository.findChatRoomsByUserId(user.getId())).thenReturn(chatRooms);

        ChatRoomDto dto1 = new ChatRoomDto();
        dto1.setChatRoomName("Chat_1");
        ChatRoomDto dto2 = new ChatRoomDto();
        dto2.setChatRoomName("Chat_2");

        when(chatRoomMapper.mapTo(chatRoom1)).thenReturn(dto1);
        when(chatRoomMapper.mapTo(chatRoom2)).thenReturn(dto2);

        List<ChatRoomDto> result = chatService.findChatsByUser(username);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> "Chat_1".equals(dto.getChatRoomName())));
        assertTrue(result.stream().anyMatch(dto -> "Chat_2".equals(dto.getChatRoomName())));
        verify(userService, times(1)).findUserByEmail(username);
        verify(chatRepository, times(1)).findChatRoomsByUserId(user.getId());
    }

    @Test
    void deleteChat_shouldDeleteChat() {
        String username = "participant@example.com";
        int chatId = 1;

        UserEntity user = new UserEntity();
        user.setId(10L);
        user.setEmail(username);

        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatRoomName("Chat_123")
                .participants(Collections.singletonList(user))
                .build();

        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.setChatRoomName(chatRoom.getChatRoomName());

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chatRoom));
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(chatRoomMapper.mapTo(chatRoom)).thenReturn(chatRoomDto);
        when(chatRoomMapper.mapFrom(chatRoomDto)).thenReturn(chatRoom);

        chatService.deleteChat(chatId, username);

        verify(chatRepository, times(1)).delete(chatRoom);
    }
}
