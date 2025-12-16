package com.kamylo.Scrtly_backend.comment.service;

import com.kamylo.Scrtly_backend.comment.mapper.CommentMapper;
import com.kamylo.Scrtly_backend.comment.web.dto.CommentDto;
import com.kamylo.Scrtly_backend.comment.domain.CommentEntity;
import com.kamylo.Scrtly_backend.like.repository.LikeRepository;
import com.kamylo.Scrtly_backend.notification.events.NotificationEvent;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.notification.domain.enums.NotificationType;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.comment.repository.CommentRepository;

import com.kamylo.Scrtly_backend.post.repository.PostRepository;
import com.kamylo.Scrtly_backend.comment.web.dto.request.CommentRequest;
import com.kamylo.Scrtly_backend.notification.service.NotificationService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.comment.repository.CommentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final LikeRepository likeRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public CommentDto createComment(CommentRequest commentRequest, String username) {
       UserEntity user = userService.findUserByEmail(username);
       PostEntity post = postRepository.findById(commentRequest.getPostId()).orElseThrow(
               () -> new CustomException(BusinessErrorCodes.POST_NOT_FOUND));

       CommentEntity.CommentEntityBuilder commentBuilder = CommentEntity.builder()
               .user(user)
               .post(post)
               .comment(commentRequest.getComment())
               .likeCount(0);

       if (commentRequest.getParentCommentId() != null) {
           CommentEntity parentComment = commentRepository.findById(commentRequest.getParentCommentId())
                   .orElseThrow(() -> new CustomException(BusinessErrorCodes.COMMENT_NOT_FOUND));

           if (!parentComment.getPost().getId().equals(post.getId())) {
               throw new CustomException(BusinessErrorCodes.COMMENT_MISMATCH);
           }
           commentBuilder.parentComment(parentComment);
       }

       CommentEntity commentEntity = commentBuilder.build();
       CommentEntity savedCommentEntity = commentRepository.save(commentEntity);

       postRepository.incrementCommentCount(post.getId());

       eventPublisher.publishEvent(new NotificationEvent(
                this,
                post.getUser().getId(),
                post.getId(),
                NotificationType.COMMENT,
                username
       ));
       return commentMapper.toDto(savedCommentEntity);
    }

    @Override
    public CommentDto updateComment(Long commentId, String content, String username) {
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.COMMENT_NOT_FOUND));
        if (validateCommentOwnership(username, comment)) {
            if (content != null && !content.trim().isEmpty()) {
                comment.setComment(content);
            }
            return commentMapper.toDto(commentRepository.save(comment));
        } else {
            throw new CustomException(BusinessErrorCodes.COMMENT_MISMATCH);
        }
    }

    @Override
    public Page<CommentDto> getCommentsByPostId(Long postId, String sortBy, Pageable pageable, String username) {
        Specification<CommentEntity> spec = Specification.where(CommentSpecification.byPostId(postId));
        if ("latest".equalsIgnoreCase(sortBy))
            spec = spec.and(CommentSpecification.orderByLatestActivity());
        else if ("popular".equalsIgnoreCase(sortBy))
            spec = spec.and(CommentSpecification.orderByLikes());

        Page<CommentEntity> commentsPage = commentRepository.findAll(spec, pageable);

        if (commentsPage.isEmpty())
            return commentsPage.map(commentMapper::toDto);

        Set<Long> likedCommentIds = new HashSet<>();
        if (username != null) {
            UserEntity user = userService.findUserByEmail(username);
            List<Long> commentIds = commentsPage.getContent().stream()
                    .map(CommentEntity::getId)
                    .toList();

            likedCommentIds.addAll(likeRepository.findCommentIdsLikedByUser(user.getId(), commentIds));
        }

        return commentsPage.map(comment -> {
            CommentDto dto = commentMapper.toDto(comment);
            dto.setLikedByUser(likedCommentIds.contains(comment.getId()));
            return dto;
        });
    }

    @Override
    public Page<CommentDto> getReplies(Long parentCommentId, Pageable pageable, String username) {
        Page<CommentEntity> repliesPage = commentRepository.findByParentCommentId(parentCommentId, pageable);

        if (repliesPage.isEmpty()) {
            return repliesPage.map(commentMapper::toDto);
        }

        Set<Long> likedReplyIds = new HashSet<>();
        if (username != null) {
            UserEntity user = userService.findUserByEmail(username);

            List<Long> replyIds = repliesPage.getContent().stream()
                    .map(CommentEntity::getId)
                    .toList();

            likedReplyIds.addAll(likeRepository.findCommentIdsLikedByUser(user.getId(), replyIds));
        }

        return repliesPage.map(reply -> {
            CommentDto dto = commentMapper.toDto(reply);
            dto.setLikedByUser(likedReplyIds.contains(reply.getId()));
            return dto;
        });
    }


    @Override
    @Transactional
    public void deleteComment(Long commentId, String username) {
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.COMMENT_NOT_FOUND));

        if (validateCommentOwnership(username, comment)) {
            Long postId = comment.getPost().getId();
            commentRepository.delete(comment);
            postRepository.decrementCommentCount(postId);
            Long recipientId = comment.getPost().getUser().getId();
            notificationService.decrementNotification(recipientId, postId, NotificationType.COMMENT);
        } else {
            throw new CustomException(BusinessErrorCodes.COMMENT_MISMATCH);
        }
    }

    private boolean validateCommentOwnership(String username, CommentEntity comment) {
        UserEntity user = userService.findUserByEmail(username);
        return comment.getUser().getId().equals(user.getId());
    }
}
