package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    @Query("select c from ChatMessage c join c.chat m where c.id=:chatId")
    public List<ChatMessage> findByChatId(@Param("chatId") Integer chatId);
}
