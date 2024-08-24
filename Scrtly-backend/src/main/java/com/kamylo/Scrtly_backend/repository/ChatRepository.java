package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.model.ChatRoom;
import com.kamylo.Scrtly_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatRoom, Integer> {

    @Query("SELECT c FROM ChatRoom c WHERE (c.firstPerson.id = :userId OR c.secondPerson.id = :userId)")
    public List<ChatRoom> findChatRoomById(@Param("userId") Long userId);

    @Query("SELECT c FROM ChatRoom c WHERE (c.firstPerson = :user1 AND c.secondPerson = :user2) " +
                  "OR (c.firstPerson = :user2 AND c.secondPerson = :user1)")
    public ChatRoom findSingleChatRoomById(@Param("user1")User user1, @Param("user2")User user2);
}
