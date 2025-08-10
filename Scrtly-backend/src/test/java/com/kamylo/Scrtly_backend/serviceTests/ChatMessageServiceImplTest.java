package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.chat.mapper.ChatMessageMapper;
import com.kamylo.Scrtly_backend.chat.mapper.ChatRoomMapper;
import com.kamylo.Scrtly_backend.chat.web.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.chat.web.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.chat.web.dto.request.ChatMessageEditRequest;
import com.kamylo.Scrtly_backend.chat.web.dto.request.SendMessageRequest;
import com.kamylo.Scrtly_backend.chat.domain.ChatMessageEntity;
import com.kamylo.Scrtly_backend.chat.domain.ChatRoomEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.chat.repository.ChatMessageRepository;
import com.kamylo.Scrtly_backend.chat.service.ChatService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.chat.service.impl.ChatMessageServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.data.domain.*;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    @Mock private ChatMessageRepository chatMessageRepository;
    @Mock private UserService userService;
    @Mock private ChatService chatService;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private ChatMessageMapper chatMessageMapper;
    @Mock private ChatRoomMapper chatRoomMapper;

    @InjectMocks private ChatMessageServiceImpl chatMessageService;

    private static final String EXCHANGE = "test.exchange";
    private static final String ROUTING_PREFIX = "chat.routing.";

    @BeforeEach
    void beforeEach() throws Exception {
        setField("chatExchangeName", EXCHANGE);
        setField("chatRoutingKeyPrefix", ROUTING_PREFIX);
    }

    @AfterEach
    void afterEach() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
        clearInvocations(chatMessageRepository, userService, chatService, rabbitTemplate, chatMessageMapper, chatRoomMapper);
    }

    private void setField(String name, Object value) throws Exception {
        Field f = ChatMessageServiceImpl.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(chatMessageService, value);
    }

    private UserEntity createUser(Long id, String email) {
        UserEntity u = new UserEntity();
        u.setId(id);
        u.setEmail(email);
        return u;
    }

    private ChatRoomDto createChatRoomDto(int id, String name) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setId(id);
        dto.setChatRoomName(name);
        return dto;
    }

    private ChatRoomEntity createChatRoomEntity(int id) {
        return ChatRoomEntity.builder().id(id).build();
    }

    private ChatMessageEntity createMessageEntity(Long id, ChatRoomEntity room, UserEntity user, String text) {
        return ChatMessageEntity.builder()
                .id(id)
                .chatRoom(room)
                .user(user)
                .messageText(text)
                .build();
    }

    private ChatMessageDto createMessageDto(Long id, int chatRoomId, String text, String status) {
        return ChatMessageDto.builder()
                .id(id)
                .chatRoomId(chatRoomId)
                .messageText(text)
                .status(status)
                .build();
    }


    @Test
    void sendMessage_shouldSendMessageSuccessfully_andPublishAfterCommit_whenTransactionActive() {
        String username = "test@example.com";
        SendMessageRequest request = new SendMessageRequest();
        request.setChatId(1);
        request.setMessage("Hello World");

        UserEntity user = createUser(100L, username);
        ChatRoomDto roomDto = createChatRoomDto(1, "Test Chat");
        ChatRoomEntity roomEntity = createChatRoomEntity(1);

        ChatMessageEntity savedEntity = createMessageEntity(null, roomEntity, user, "Hello World");
        ChatMessageDto mappedDto = createMessageDto(1L, 1, "Hello World", "NEW");

        when(userService.findUserByEmail(username)).thenReturn(user);
        when(chatService.getChatById(request.getChatId(), username)).thenReturn(roomDto);
        when(chatRoomMapper.toEntity(roomDto)).thenReturn(roomEntity);
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenReturn(savedEntity);
        when(chatMessageMapper.toDto(savedEntity)).thenReturn(mappedDto);

        TransactionSynchronizationManager.initSynchronization();

        CompletableFuture<ChatMessageDto> future = chatMessageService.sendMessageAsync(request, username);
        ChatMessageDto result = future.join();

        assertNotNull(result);
        assertEquals("Hello World", result.getMessageText());
        assertEquals("NEW", result.getStatus());
        verify(userService).findUserByEmail(username);
        verify(chatService).getChatById(request.getChatId(), username);
        verify(chatMessageRepository).save(any(ChatMessageEntity.class));
        verify(chatMessageMapper).toDto(savedEntity);

        List<TransactionSynchronization> syncs = TransactionSynchronizationManager.getSynchronizations();
        assertFalse(syncs.isEmpty(), "Powinna istnieć zarejestrowana synchronizacja");

        for (TransactionSynchronization s : syncs) {
            s.afterCommit();
        }

        String expectedRoutingKey = ROUTING_PREFIX + mappedDto.getChatRoomId();
        verify(rabbitTemplate).convertAndSend(EXCHANGE, expectedRoutingKey, mappedDto);

        TransactionSynchronizationManager.clearSynchronization();
    }

    @Test
    void sendMessage_shouldNotPublish_whenNoTransactionActive() {
        String username = "no-tx@example.com";
        SendMessageRequest request = new SendMessageRequest();
        request.setChatId(2);
        request.setMessage("No TX");

        UserEntity user = createUser(200L, username);
        ChatRoomDto roomDto = createChatRoomDto(2, "Chat");
        ChatRoomEntity roomEntity = createChatRoomEntity(2);
        ChatMessageEntity savedEntity = createMessageEntity(2L, roomEntity, user, "No TX");
        ChatMessageDto dto = createMessageDto(2L, 2, "No TX", "NEW");

        when(userService.findUserByEmail(username)).thenReturn(user);
        when(chatService.getChatById(request.getChatId(), username)).thenReturn(roomDto);
        when(chatRoomMapper.toEntity(roomDto)).thenReturn(roomEntity);
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenReturn(savedEntity);
        when(chatMessageMapper.toDto(savedEntity)).thenReturn(dto);

        CompletableFuture<ChatMessageDto> future = chatMessageService.sendMessageAsync(request, username);
        ChatMessageDto result = future.join();

        assertNotNull(result);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }

    @Test
    void editMessage_shouldThrow_whenChatIdMismatch() {
        ChatMessageEditRequest request = new ChatMessageEditRequest();
        request.setId(1L);
        request.setMessage("Updated message");
        Integer providedChatId = 2;
        String username = "user@example.com";

        UserEntity user = createUser(100L, username);
        ChatRoomEntity chatRoom = createChatRoomEntity(1);

        ChatMessageEntity messageEntity = createMessageEntity(1L, chatRoom, user, "Original");
        when(chatMessageRepository.findById(request.getId())).thenReturn(Optional.of(messageEntity));

        CustomException ex = assertThrows(CustomException.class, () ->
                chatMessageService.editMessageAsync(request, providedChatId, username).join()
        );
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_NOT_IN_CHAT, ex.getErrorCode());
    }

    @Test
    void editMessage_shouldEditSuccessfully_whenOwnerAndChatMatches() {
        ChatMessageEditRequest request = new ChatMessageEditRequest();
        request.setId(10L);
        request.setMessage("Edited");
        int chatId = 5;
        String username = "owner@example.com";

        UserEntity user = createUser(123L, username);
        ChatRoomEntity chatRoom = createChatRoomEntity(chatId);

        ChatMessageEntity original = createMessageEntity(10L, chatRoom, user, "Orig");
        ChatMessageEntity saved = createMessageEntity(10L, chatRoom, user, "Edited");
        ChatMessageDto dto = createMessageDto(10L, chatId, "Edited", "EDITED");

        when(chatMessageRepository.findById(request.getId())).thenReturn(Optional.of(original));
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenReturn(saved);
        when(chatMessageMapper.toDto(saved)).thenReturn(dto);

        ChatMessageDto result = chatMessageService.editMessageAsync(request, chatId, username).join();

        assertNotNull(result);
        assertEquals("Edited", result.getMessageText());
        assertEquals("EDITED", result.getStatus());
        assertEquals(chatId, result.getChatRoomId());
        verify(chatMessageRepository).save(original);
    }

    @Test
    void editMessage_shouldThrow_whenMessageNotFound() {
        ChatMessageEditRequest request = new ChatMessageEditRequest();
        request.setId(99L);
        request.setMessage("Updated");
        int chatId = 1;
        String username = "u@example.com";

        when(chatMessageRepository.findById(request.getId())).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () ->
                chatMessageService.editMessageAsync(request, chatId, username).join()
        );
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void editMessage_shouldThrow_whenUserNotOwner() {
        ChatMessageEditRequest request = new ChatMessageEditRequest();
        request.setId(2L);
        request.setMessage("X");
        int chatId = 3;
        String username = "caller@example.com";

        UserEntity owner = createUser(999L, "owner@example.com");
        UserEntity caller = createUser(888L, username);
        ChatRoomEntity chatRoom = createChatRoomEntity(chatId);
        ChatMessageEntity message = createMessageEntity(2L, chatRoom, owner, "orig");

        when(chatMessageRepository.findById(request.getId())).thenReturn(Optional.of(message));
        when(userService.findUserByEmail(username)).thenReturn(caller);

        CustomException ex = assertThrows(CustomException.class, () ->
                chatMessageService.editMessageAsync(request, chatId, username).join()
        );
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_MISMATCH, ex.getErrorCode());
    }

    @Test
    void deleteMessage_shouldDelete_whenOwnerAndChatMatches() {
        String username = "owner@example.com";
        Long messageId = 7L;
        Integer chatId = 11;

        UserEntity owner = createUser(321L, username);
        ChatRoomEntity chatRoom = createChatRoomEntity(chatId);
        ChatMessageEntity message = createMessageEntity(messageId, chatRoom, owner, "to delete");
        when(chatMessageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(userService.findUserByEmail(username)).thenReturn(owner);

        ChatMessageDto result = chatMessageService.deleteMessageAsync(messageId, chatId, username).join();

        verify(chatMessageRepository).deleteById(messageId);
        assertEquals("DELETED", result.getStatus());
        assertEquals(chatId, result.getChatRoomId());
    }

    @Test
    void deleteMessage_shouldThrow_whenMessageNotFound() {
        when(chatMessageRepository.findById(100L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () ->
                chatMessageService.deleteMessageAsync(100L, 1, "u@example.com").join()
        );
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_NOT_FOUND, ex.getErrorCode());
        verify(chatMessageRepository, never()).deleteById(any());
    }

    @Test
    void deleteMessage_shouldThrow_whenUserNotOwner() {
        String caller = "someone";
        UserEntity owner = createUser(50L, "owner");
        UserEntity callerUser = createUser(60L, caller);
        ChatRoomEntity chatRoom = createChatRoomEntity(5);
        ChatMessageEntity message = createMessageEntity(55L, chatRoom, owner, "text");

        when(chatMessageRepository.findById(55L)).thenReturn(Optional.of(message));
        when(userService.findUserByEmail(caller)).thenReturn(callerUser);

        CustomException ex = assertThrows(CustomException.class, () ->
                chatMessageService.deleteMessageAsync(55L, 5, caller).join()
        );
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_MISMATCH, ex.getErrorCode());
        verify(chatMessageRepository, never()).deleteById(any());
    }

    @Test
    void deleteMessage_shouldThrow_whenChatIdMismatch() {
        Long messageId = 2L;
        Integer providedChatId = 999;
        UserEntity user = createUser(1L, "u");
        ChatRoomEntity room = createChatRoomEntity(1);
        ChatMessageEntity msg = createMessageEntity(messageId, room, user, "x");

        when(chatMessageRepository.findById(messageId)).thenReturn(Optional.of(msg));

        CustomException ex = assertThrows(CustomException.class, () ->
                chatMessageService.deleteMessageAsync(messageId, providedChatId, "u").join()
        );
        assertEquals(BusinessErrorCodes.CHAT_MESSAGE_NOT_IN_CHAT, ex.getErrorCode());
    }

    @Test
    void getChatMessages_shouldReturnMessages() {
        int chatId = 42;
        ChatMessageEntity entity = createMessageEntity(100L, null, null, "Test Message");
        ChatMessageDto dto = createMessageDto(100L, chatId, "Test Message", null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ChatMessageEntity> pageEntity = new PageImpl<>(Collections.singletonList(entity), pageable, 1);

        when(chatMessageRepository.findByChatRoomIdOrderByCreateDateDesc(chatId, pageable)).thenReturn(pageEntity);
        when(chatMessageMapper.toDto(entity)).thenReturn(dto);

        Page<ChatMessageDto> res = chatMessageService.getChatMessages(chatId, pageable);

        assertNotNull(res);
        assertEquals(1, res.getTotalElements());
        assertEquals("Test Message", res.getContent().get(0).getMessageText());
        verify(chatMessageRepository).findByChatRoomIdOrderByCreateDateDesc(chatId, pageable);
    }
}
