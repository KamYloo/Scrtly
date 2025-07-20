package com.kamylo.Scrtly_backend.comment.domain;

import com.kamylo.Scrtly_backend.like.domain.LikeEntity;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comment")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EntityListeners(AuditingEntityListener.class)
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message = "Comment must not be blank")
    @Size(max = 1000, message = "Comment text cannot exceed 1000 characters")
    @Column(nullable = false)
    private String comment;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<LikeEntity> likes = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CommentEntity parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CommentEntity> replies = new HashSet<>();
}
