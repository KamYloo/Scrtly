package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
     List<ChatMessage> findByChatRoomId(@Param("chatId") Integer chatId);
}
