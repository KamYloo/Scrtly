package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.request.SendMessageRequest;
import java.util.List;

public interface ChatMessageService {
     ChatMessageDto sendMessage(SendMessageRequest request, String username);
     List<ChatMessageDto> getChatsMessages(Integer chatId, String username);
     void deleteChatMessage(Integer messageId, String username);
}
