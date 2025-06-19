package com.kamylo.Scrtly_backend.chat.repository;

import com.kamylo.Scrtly_backend.chat.domain.ChatMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
     @EntityGraph(attributePaths = {"user", "chatRoom"})
     Page<ChatMessageEntity> findByChatRoomIdOrderByCreateDateDesc(Integer chatId, Pageable pageable);
}
