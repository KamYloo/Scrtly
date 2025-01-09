package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Integer> {
     List<ChatMessageEntity> findByChatRoomId(Integer chatId);
}
