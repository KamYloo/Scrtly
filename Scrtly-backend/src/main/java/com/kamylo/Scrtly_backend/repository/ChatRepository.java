package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.ChatRoomEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatRoomEntity, Integer> {

    @EntityGraph(attributePaths = "participants")
    @Query("SELECT c FROM ChatRoomEntity c JOIN c.participants p WHERE p.id = :userId")
    List<ChatRoomEntity> findChatRoomsByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM ChatRoomEntity c WHERE :user1 MEMBER OF c.participants AND :user2 MEMBER OF c.participants")
    ChatRoomEntity findSingleChatRoomByUsers(@Param("user1") UserEntity user1, @Param("user2") UserEntity user2);
}
