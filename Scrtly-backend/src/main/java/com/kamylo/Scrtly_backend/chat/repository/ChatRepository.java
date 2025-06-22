package com.kamylo.Scrtly_backend.chat.repository;

import com.kamylo.Scrtly_backend.chat.domain.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatRoomEntity, Integer> {

    @Query("SELECT c FROM ChatRoomEntity c JOIN c.participants p WHERE p.id = :userId")
    List<ChatRoomEntity> findChatRoomsByUserId(@Param("userId") Long userId);
}
