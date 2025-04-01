package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.dto.request.ChatMessageEditRequest;
import com.kamylo.Scrtly_backend.dto.request.DeleteMessageRequest;
import com.kamylo.Scrtly_backend.dto.request.SendMessageRequest;
import com.kamylo.Scrtly_backend.service.ChatMessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@RestController
public class RealTimeChat {
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/sendMessage/{chatId}")
    public CompletableFuture<ChatMessageDto> sendMessage(@DestinationVariable Integer chatId, @Payload SendMessageRequest request, Principal principal) {
        request.setChatId(chatId);
        return chatMessageService.sendMessageAsync(request, principal.getName());
    }

    @MessageMapping("/chat/editMessage/{chatId}")
    public CompletableFuture<ChatMessageDto> editMessage(@DestinationVariable Integer chatId,
                            @Payload ChatMessageEditRequest editRequest,
                            Principal principal) {
        return chatMessageService.editMessageAsync(editRequest, chatId, principal.getName());
    }

    @MessageMapping("/chat/deleteMessage/{chatId}")
    public CompletableFuture<ChatMessageDto> deleteMessage(@DestinationVariable Integer chatId,
                              @Payload DeleteMessageRequest deleteRequest,
                              Principal principal) {
        return chatMessageService.deleteMessageAsync(deleteRequest.getMessageId(), chatId, principal.getName());
    }
}
