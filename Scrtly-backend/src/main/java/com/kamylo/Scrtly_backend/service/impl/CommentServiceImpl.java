package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.CommentDto;
import com.kamylo.Scrtly_backend.entity.CommentEntity;
import com.kamylo.Scrtly_backend.entity.PostEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.entity.enums.NotificationType;
import com.kamylo.Scrtly_backend.events.NotificationEvent;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.CommentRepository;

import com.kamylo.Scrtly_backend.repository.PostRepository;
import com.kamylo.Scrtly_backend.request.CommentRequest;
import com.kamylo.Scrtly_backend.service.CommentService;
import com.kamylo.Scrtly_backend.service.NotificationService;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.specification.CommentSpecification;
import com.kamylo.Scrtly_backend.utils.UserLikeChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostRepository postRepository;
    private final Mapper<CommentEntity, CommentDto> commentMapper;
    private final UserLikeChecker userLikeChecker;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public CommentDto createComment(CommentRequest commentRequest, String username) {
       UserEntity user = userService.findUserByEmail(username);
       PostEntity post = postRepository.findById(commentRequest.getPostId()).orElseThrow(
               () -> new CustomException(BusinessErrorCodes.POST_NOT_FOUND));

       CommentEntity commentEntity = CommentEntity.builder()
               .user(user)
               .post(post)
               .comment(commentRequest.getComment())
               .build();

       CommentEntity savedCommentEntity = commentRepository.save(commentEntity);

        eventPublisher.publishEvent(new NotificationEvent(
                this,
                post.getUser().getId(),
                post.getId(),
                NotificationType.COMMENT,
                username
        ));
       return commentMapper.mapTo(savedCommentEntity);
    }

    @Override
    public CommentDto updateComment(Long commentId, String content, String username) {
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.COMMENT_NOT_FOUND));
        if (validateCommentOwnership(username, comment)) {
            if (content != null && !content.isEmpty()) {
                comment.setComment(content);
            }
            return commentMapper.mapTo(commentRepository.save(comment));
        } else {
            throw new CustomException(BusinessErrorCodes.COMMENT_MISMATCH);
        }
    }

    @Override
    public Page<CommentDto> getCommentsByPostId(Long postId, String sortBy, Pageable pageable, String username) {
        UserEntity user = userService.findUserByEmail(username);
        Specification<CommentEntity> spec = Specification.where(CommentSpecification.byPostId(postId));
        if ("latest".equalsIgnoreCase(sortBy)) {
            spec = spec.and(CommentSpecification.orderByLatestActivity());
        } else if ("popular".equalsIgnoreCase(sortBy)) {
            spec = spec.and(CommentSpecification.orderByLikes());
        }

        return commentRepository.findAll(spec, pageable).map(commentEntity -> {
            CommentDto commentDto = commentMapper.mapTo(commentEntity);
            commentDto.setLikedByUser(userLikeChecker.isCommentLikedByUser(commentEntity, user.getId()));
            return commentDto;
        });
    }


    @Override
    @Transactional
    public void deleteComment(Long commentId, String username) {
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.COMMENT_NOT_FOUND));

        if (validateCommentOwnership(username, comment)) {
            commentRepository.delete(comment);
            Long postId = comment.getPost().getId();
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
