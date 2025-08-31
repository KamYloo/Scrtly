package com.kamylo.Scrtly_backend.chat.web.controller;

import com.kamylo.Scrtly_backend.chat.web.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.chat.web.dto.request.ChatMessageEditRequest;
import com.kamylo.Scrtly_backend.chat.web.dto.request.DeleteMessageRequest;
import com.kamylo.Scrtly_backend.chat.web.dto.request.SendMessageRequest;
import com.kamylo.Scrtly_backend.chat.service.ChatMessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@RestController
public class RealTimeChat {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat/sendMessage/{chatId}")
    public CompletableFuture<ChatMessageDto> sendMessage(
            @DestinationVariable @Positive Integer chatId,
            @Payload @Valid SendMessageRequest request,
            Principal principal) {
        request.setChatId(chatId);
        return chatMessageService.sendMessageAsync(request, principal.getName());
    }

    @MessageMapping("/chat/editMessage/{chatId}")
    public CompletableFuture<ChatMessageDto> editMessage(
            @DestinationVariable @Positive Integer chatId,
            @Payload @Valid ChatMessageEditRequest editRequest,
            Principal principal) {
        return chatMessageService.editMessageAsync(editRequest, chatId, principal.getName());
    }

    @MessageMapping("/chat/deleteMessage/{chatId}")
    public CompletableFuture<ChatMessageDto> deleteMessage(
            @DestinationVariable @Positive Integer chatId,
            @Payload @Valid DeleteMessageRequest deleteRequest,
            Principal principal) {
        return chatMessageService.deleteMessageAsync(deleteRequest.getMessageId(), chatId, principal.getName());
    }

    @MessageExceptionHandler(Exception.class)
    public void handleMessagingException(Exception ex, Principal principal) {
        String user = (principal != null ? principal.getName() : "unknown");
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Server error";
        simpMessagingTemplate.convertAndSendToUser(user, "/queue/errors", errorMessage);
    }
}
