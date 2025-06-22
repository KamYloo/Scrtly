package com.kamylo.Scrtly_backend.notification.repository;

import com.kamylo.Scrtly_backend.notification.domain.NotificationEntity;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.notification.domain.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    Optional<NotificationEntity> findByRecipientAndTypeAndPost(UserEntity recipient, NotificationType type, PostEntity post);

    @EntityGraph(attributePaths = {"recipient", "post"})
    Page<NotificationEntity> findByRecipientIdOrderByUpdatedDateDescCreatedDateDesc(Long recipientId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.post = ?1")
    void deleteAllByPost(PostEntity post);
}
