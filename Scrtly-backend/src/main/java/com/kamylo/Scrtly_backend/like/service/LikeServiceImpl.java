package com.kamylo.Scrtly_backend.like.service;

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
import com.kamylo.Scrtly_backend.like.mapper.CommentLikeMapperImpl;
import com.kamylo.Scrtly_backend.like.mapper.PostLikeMapperImpl;
import com.kamylo.Scrtly_backend.comment.repository.CommentRepository;
import com.kamylo.Scrtly_backend.like.repository.LikeRepository;
import com.kamylo.Scrtly_backend.post.repository.PostRepository;
import com.kamylo.Scrtly_backend.notification.service.NotificationService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.common.utils.UserLikeChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final UserService userService;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeMapperImpl postLikeMapper;
    private final CommentLikeMapperImpl commentLikeMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;
    private final UserLikeChecker userLikeChecker;

   /* @Override
    public List<LikeEntity> getLikesByPost(Long postId) throws PostException {
       Optional<PostEntity> post = postRepository.findById(postId);
       if (post.isPresent()) {
           return likeRepository.findByPostId(post.get().getId());
       }
       throw new PostException("PostEntity not found with id: " + postId);
    }

    @Override
    public List<LikeEntity> getLikesByComment(Long commentId) throws CommentException {
       CommentEntity commentEntity = commentService.findCommentById(commentId);
       if (commentEntity != null) {
           return likeRepository.findByCommentId(commentId);
       }
      throw new CommentException("CommentEntity not found with id: " + commentId);
    }*/

    @Override
    @Transactional
    public PostStatsDto likePost(Long postId, String username) {
        UserEntity user = userService.findUserByEmail(username);
        LikeEntity checkLikeExistPost = likeRepository.isLikeExistPost(user.getId(), postId);

        if (checkLikeExistPost != null) {
            likeRepository.deleteById(checkLikeExistPost.getId());
            PostEntity post = postRepository.findById(postId).orElseThrow(
                    () -> new CustomException(BusinessErrorCodes.POST_NOT_FOUND));
            post.getLikes().remove(checkLikeExistPost);
            notificationService.decrementNotification(post.getUser().getId(), postId, NotificationType.LIKE);
            PostStatsDto postStatsDto = postLikeMapper.mapTo(checkLikeExistPost);
            postStatsDto.setLikedByUser(userLikeChecker.isPostLikedByUser(post, user.getId()));
            return postStatsDto;
        }

        PostEntity post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.POST_NOT_FOUND));

        LikeEntity like = LikeEntity.builder()
                .user(user)
                .post(post)
                .build();

        LikeEntity savedLikeEntity = likeRepository.save(like);
        post.getLikes().add(savedLikeEntity);
        postRepository.save(post);

        eventPublisher.publishEvent(new NotificationEvent(
                this,
                post.getUser().getId(),
                post.getId(),
                NotificationType.LIKE,
                username
        ));

        PostStatsDto postStatsDto = postLikeMapper.mapTo(like);
        postStatsDto.setLikedByUser(userLikeChecker.isPostLikedByUser(post, user.getId()));
        return postStatsDto;
    }

    @Override
    @Transactional
    public CommentStatsDto likeComment(Long commentId, String username) {
        UserEntity user = userService.findUserByEmail(username);
        LikeEntity checkLikeExistComment = likeRepository.isLikeExistComment(user.getId(), commentId);

        if (checkLikeExistComment != null) {
            likeRepository.deleteById(checkLikeExistComment.getId());
            CommentEntity comment = commentRepository.findById(commentId).orElseThrow(
                    () -> new CustomException(BusinessErrorCodes.COMMENT_NOT_FOUND));
            comment.getLikes().remove(checkLikeExistComment);
            CommentStatsDto commentStatsDto = commentLikeMapper.mapTo(checkLikeExistComment);
            commentStatsDto.setLikedByUser(userLikeChecker.isCommentLikedByUser(comment, user.getId()));
            return commentStatsDto;
        }

        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.COMMENT_NOT_FOUND));

        LikeEntity like = LikeEntity.builder()
                .user(user)
                .comment(comment)
                .build();

        LikeEntity savedLikeEntity = likeRepository.save(like);
        comment.getLikes().add(savedLikeEntity);
        commentRepository.save(comment);

        CommentStatsDto commentStatsDto = commentLikeMapper.mapTo(like);
        commentStatsDto.setLikedByUser(userLikeChecker.isCommentLikedByUser(comment, user.getId()));
        return commentStatsDto;
    }
}
