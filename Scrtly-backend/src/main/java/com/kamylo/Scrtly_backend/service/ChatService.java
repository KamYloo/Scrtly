package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.ChatException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.ChatRoom;
import com.kamylo.Scrtly_backend.model.User;

import java.util.List;

public interface ChatService {
    public ChatRoom createChat (User reqUser, Long userId2) throws UserException;

    public ChatRoom findChatById(Integer chatId) throws ChatException;
    public List<ChatRoom> findAllChatByUserId(Long userId) throws UserException;
    public void deleteChat (Integer chatId, Long userId) throws UserException, ChatException;

}
