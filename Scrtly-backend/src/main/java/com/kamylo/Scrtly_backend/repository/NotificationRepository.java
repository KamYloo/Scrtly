package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.dto.NotificationDto;
import com.kamylo.Scrtly_backend.entity.NotificationEntity;
import com.kamylo.Scrtly_backend.entity.PostEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.entity.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    Optional<NotificationEntity> findByRecipientAndTypeAndPost(UserEntity recipient, NotificationType type, PostEntity post);
    Page<NotificationEntity> findByRecipientIdOrderByUpdatedDateDesc(Long recipientId, Pageable pageable);
}
