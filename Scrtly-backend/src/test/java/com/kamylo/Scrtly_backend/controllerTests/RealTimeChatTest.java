package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.chat.service.ChatMessageService;
import com.kamylo.Scrtly_backend.chat.web.controller.RealTimeChat;
import com.kamylo.Scrtly_backend.chat.web.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.chat.web.dto.request.ChatMessageEditRequest;
import com.kamylo.Scrtly_backend.chat.web.dto.request.DeleteMessageRequest;
import com.kamylo.Scrtly_backend.chat.web.dto.request.SendMessageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealTimeChatTest {

    @Mock
    private ChatMessageService chatMessageService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private RealTimeChat controller;

    @Mock
    private Principal principal;

    @Test
    void sendMessage_invokesService_andReturnsFutureWithDto() throws Exception {
        int chatId = 7;
        when(principal.getName()).thenReturn("alice");

        SendMessageRequest req = new SendMessageRequest();
        ChatMessageDto dto = mock(ChatMessageDto.class);
        when(chatMessageService.sendMessageAsync(any(SendMessageRequest.class), eq("alice")))
                .thenReturn(CompletableFuture.completedFuture(dto));

        CompletableFuture<ChatMessageDto> fut = controller.sendMessage(chatId, req, principal);
        assertSame(dto, fut.get());

        ArgumentCaptor<SendMessageRequest> captor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(chatMessageService).sendMessageAsync(captor.capture(), eq("alice"));
        assertEquals(Integer.valueOf(chatId), captor.getValue().getChatId());
    }

    @Test
    void editMessage_invokesService_andReturnsFutureWithDto() throws Exception {
        int chatId = 5;
        when(principal.getName()).thenReturn("bob");

        ChatMessageEditRequest editReq = mock(ChatMessageEditRequest.class);
        ChatMessageDto dto = mock(ChatMessageDto.class);
        when(chatMessageService.editMessageAsync(editReq, chatId, "bob"))
                .thenReturn(CompletableFuture.completedFuture(dto));

        CompletableFuture<ChatMessageDto> fut = controller.editMessage(chatId, editReq, principal);
        assertSame(dto, fut.get());
        verify(chatMessageService).editMessageAsync(editReq, chatId, "bob");
    }

    @Test
    void deleteMessage_invokesService_withMessageId_andReturnsFuture() throws Exception {
        int chatId = 9;
        when(principal.getName()).thenReturn("carol");

        DeleteMessageRequest delReq = mock(DeleteMessageRequest.class);
        when(delReq.getMessageId()).thenReturn(123L);
        ChatMessageDto dto = mock(ChatMessageDto.class);
        when(chatMessageService.deleteMessageAsync(123L, chatId, "carol"))
                .thenReturn(CompletableFuture.completedFuture(dto));

        CompletableFuture<ChatMessageDto> fut = controller.deleteMessage(chatId, delReq, principal);
        assertSame(dto, fut.get());
        verify(chatMessageService).deleteMessageAsync(123L, chatId, "carol");
    }

    @Test
    void handleMessagingException_sendsErrorToUserQueue() {
        when(principal.getName()).thenReturn("dan");
        Exception ex = new Exception("boom");

        controller.handleMessagingException(ex, principal);

        verify(simpMessagingTemplate).convertAndSendToUser("dan", "/queue/errors", "boom");
    }
}
