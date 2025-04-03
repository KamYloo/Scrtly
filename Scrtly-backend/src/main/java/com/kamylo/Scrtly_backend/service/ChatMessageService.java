package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.dto.request.ChatMessageEditRequest;
import com.kamylo.Scrtly_backend.dto.request.SendMessageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.CompletableFuture;

public interface ChatMessageService {
     CompletableFuture<ChatMessageDto> sendMessageAsync(SendMessageRequest request, String username);
     CompletableFuture<ChatMessageDto> editMessageAsync(ChatMessageEditRequest request, Integer chatId, String username);
     CompletableFuture<ChatMessageDto> deleteMessageAsync(Long messageId, Integer chatId, String username);
     Page<ChatMessageDto> getChatMessages(Integer chatId, Pageable pageable);
}
