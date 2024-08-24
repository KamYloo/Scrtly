package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.ChatException;
import com.kamylo.Scrtly_backend.exception.MessageException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.ChatMessage;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.request.SendMessageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatMessageService {
    public ChatMessage sendMessage(SendMessageRequest request) throws UserException, ChatException;

    public List<ChatMessage> getChatsMessages(Integer chatId) throws ChatException;

    public ChatMessage findChatMessageById(Integer messageId) throws MessageException;

    public void deleteChatMessageById(Integer messageId, User reqUser) throws UserException, MessageException;
}
