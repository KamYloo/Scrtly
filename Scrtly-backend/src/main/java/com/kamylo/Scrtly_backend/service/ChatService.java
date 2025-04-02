package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.dto.request.ChatRoomRequest;

import java.util.List;


public interface ChatService {
     ChatRoomDto createChat (String username, ChatRoomRequest chatRoomRequest);
     ChatRoomDto getChatById(Integer chatId, String username);
     List<ChatRoomDto> findChatsByUser(String username);
     void deleteChat (Integer chatId, String username);

}
