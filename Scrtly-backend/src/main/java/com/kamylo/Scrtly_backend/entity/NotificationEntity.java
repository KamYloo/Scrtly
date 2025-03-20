package com.kamylo.Scrtly_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kamylo.Scrtly_backend.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EntityListeners(AuditingEntityListener.class)
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private boolean seen;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Integer count;


    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserEntity recipient;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnoreProperties({"likes", "comments"})
    private PostEntity post;

}
