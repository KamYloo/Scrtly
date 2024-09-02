package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.ChatException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.ChatRoom;
import com.kamylo.Scrtly_backend.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {
     ChatRoom createChat (User reqUser, Long userId2) throws UserException;
     ChatRoom findChatById(Integer chatId) throws ChatException;
     List<ChatRoom> findAllChatByUserId(Long userId) throws UserException;
     void deleteChat (Integer chatId, Long userId) throws UserException, ChatException;

}
