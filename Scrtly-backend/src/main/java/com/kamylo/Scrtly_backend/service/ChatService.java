package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.ChatRoomDto;

import java.util.List;


public interface ChatService {
     ChatRoomDto createChat (String username, List<Long> userIds);
     ChatRoomDto getChatById(Integer chatId, String username);
     List<ChatRoomDto> findChatsByUser(String username);
     void deleteChat (Integer chatId, String username);

}
