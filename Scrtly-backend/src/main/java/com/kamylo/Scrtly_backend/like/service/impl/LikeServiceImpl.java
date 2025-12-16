package com.kamylo.Scrtly_backend.like.service.impl;

import com.kamylo.Scrtly_backend.like.mapper.CommentLikeMapper;
import com.kamylo.Scrtly_backend.like.mapper.PostLikeMapper;
import com.kamylo.Scrtly_backend.like.service.LikeService;
import com.kamylo.Scrtly_backend.like.web.dto.PostStatsDto;
import com.kamylo.Scrtly_backend.like.web.dto.CommentStatsDto;
import com.kamylo.Scrtly_backend.comment.domain.CommentEntity;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.like.domain.LikeEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.notification.domain.enums.NotificationType;
import com.kamylo.Scrtly_backend.notification.events.NotificationEvent;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.comment.repository.CommentRepository;
import com.kamylo.Scrtly_backend.like.repository.LikeRepository;
import com.kamylo.Scrtly_backend.post.repository.PostRepository;
import com.kamylo.Scrtly_backend.notification.service.NotificationService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final UserService userService;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeMapper postLikeMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PostStatsDto likePost(Long postId, String username) {
        UserEntity user = userService.findUserByEmail(username);
        Optional<LikeEntity> checkLikeExistPost = likeRepository.findByUserIdAndPostId(user.getId(), postId);

        PostEntity post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.POST_NOT_FOUND));

        if (checkLikeExistPost.isPresent()) {
            likeRepository.deleteById(checkLikeExistPost.get().getId());
            postRepository.decrementLikeCount(postId);
            post.setLikeCount(post.getLikeCount() - 1);
            notificationService.decrementNotification(post.getUser().getId(), postId, NotificationType.LIKE);
            PostStatsDto postStatsDto = postLikeMapper.toDto(checkLikeExistPost.get());
            postStatsDto.setLikeCount(post.getLikeCount());
            postStatsDto.setLikedByUser(false);
            return postStatsDto;
        } else {
            LikeEntity like = LikeEntity.builder()
                    .user(user)
                    .post(post)
                    .build();

            likeRepository.save(like);
            postRepository.incrementLikeCount(postId);
            post.setLikeCount(post.getLikeCount() + 1);

            eventPublisher.publishEvent(new NotificationEvent(
                    this,
                    post.getUser().getId(),
                    post.getId(),
                    NotificationType.LIKE,
                    username
            ));

            PostStatsDto postStatsDto = postLikeMapper.toDto(like);
            postStatsDto.setLikeCount(post.getLikeCount());
            postStatsDto.setLikedByUser(true);
            return postStatsDto;
        }
    }

    @Override
    @Transactional
    public CommentStatsDto likeComment(Long commentId, String username) {
        UserEntity user = userService.findUserByEmail(username);
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.COMMENT_NOT_FOUND));

        Optional<LikeEntity> checkLikeExistComment = likeRepository.findByUserIdAndCommentId(user.getId(), commentId);

        if (checkLikeExistComment.isPresent()) {
            likeRepository.deleteById(checkLikeExistComment.get().getId());
            commentRepository.decrementLikeCount(commentId);
            comment.setLikeCount(comment.getLikeCount() - 1);
            CommentStatsDto commentStatsDto = commentLikeMapper.toDto(checkLikeExistComment.get());
            commentStatsDto.setLikeCount(comment.getLikeCount());
            commentStatsDto.setLikedByUser(false);
            return commentStatsDto;
        } else {
            LikeEntity like = LikeEntity.builder()
                    .user(user)
                    .comment(comment)
                    .build();

            likeRepository.save(like);
            commentRepository.incrementLikeCount(commentId);
            comment.setLikeCount(comment.getLikeCount() + 1);

            CommentStatsDto commentStatsDto = commentLikeMapper.toDto(like);
            commentStatsDto.setLikeCount(comment.getLikeCount());
            commentStatsDto.setLikedByUser(true);
            return commentStatsDto;
        }
    }
}
