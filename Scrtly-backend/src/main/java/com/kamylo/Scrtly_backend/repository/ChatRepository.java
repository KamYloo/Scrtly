package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.ChatRoomEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatRoomEntity, Integer> {

    @Query("SELECT c FROM ChatRoomEntity c WHERE (c.firstPerson.id = :userId OR c.secondPerson.id = :userId)")
     List<ChatRoomEntity> findChatRoomById(@Param("userId") Long userId);

    @Query("SELECT c FROM ChatRoomEntity c WHERE (c.firstPerson = :user1 AND c.secondPerson = :user2) " +
                  "OR (c.firstPerson = :user2 AND c.secondPerson = :user1)")
    ChatRoomEntity findSingleChatRoomById(@Param("user1") UserEntity user1, @Param("user2") UserEntity user2);
}
