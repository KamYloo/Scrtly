package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.model.ChatRoom;

public interface ChatService {
    public ChatRoom createChat (Integer reqUser, Integer userId2);
}
