package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.dto.request.ChatMessageEditRequest;
import com.kamylo.Scrtly_backend.dto.request.SendMessageRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ChatMessageService {
     CompletableFuture<ChatMessageDto> sendMessageAsync(SendMessageRequest request, String username);
     CompletableFuture<ChatMessageDto> editMessageAsync(ChatMessageEditRequest request, String username);
     CompletableFuture<ChatMessageDto> deleteMessageAsync(Long messageId, String username);
     List<ChatMessageDto> getChatsMessages(Integer chatId, String username);
}
