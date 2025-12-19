package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.comment.domain.CommentEntity;
import com.kamylo.Scrtly_backend.comment.mapper.CommentMapper;
import com.kamylo.Scrtly_backend.comment.repository.CommentRepository;
import com.kamylo.Scrtly_backend.comment.service.CommentServiceImpl;
import com.kamylo.Scrtly_backend.comment.web.dto.CommentDto;
import com.kamylo.Scrtly_backend.comment.web.dto.request.CommentRequest;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.like.repository.LikeRepository;
import com.kamylo.Scrtly_backend.notification.domain.enums.NotificationType;
import com.kamylo.Scrtly_backend.notification.events.NotificationEvent;
import com.kamylo.Scrtly_backend.notification.service.NotificationService;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.post.repository.PostRepository;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks private CommentServiceImpl commentService;
    @Mock private CommentRepository commentRepository;
    @Mock private UserService userService;
    @Mock private PostRepository postRepository;
    @Mock private CommentMapper commentMapper;
    @Mock private LikeRepository likeRepository; // Nowy mock
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private NotificationService notificationService;

    private UserEntity user;
    private PostEntity post;
    private CommentEntity comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");

        post = new PostEntity();
        post.setId(2L);
        post.setUser(user);

        comment = CommentEntity.builder()
                .id(3L)
                .user(user)
                .post(post)
                .comment("Test Comment")
                .likeCount(0)
                .build();

        commentDto = new CommentDto();
        commentDto.setId(3L);
        commentDto.setComment("Test Comment");
    }

    @Test
    void createComment_success() {
        CommentRequest req = new CommentRequest(2L, "Hello", null);
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(req, user.getEmail());

        assertThat(result).isEqualTo(commentDto);
        verify(postRepository).incrementCommentCount(post.getId());
        verify(eventPublisher).publishEvent(any(NotificationEvent.class));
    }

    @Test
    void createComment_postNotFound_shouldThrow() {
        CommentRequest req = new CommentRequest(2L, "Hello", null);
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class,
                () -> commentService.createComment(req, user.getEmail()));
    }

    @Test
    void createComment_replyAndMismatch_shouldThrow() {
        CommentRequest req = new CommentRequest(2L, "Hello", 99L);
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        CommentEntity parent = CommentEntity.builder()
                .id(99L)
                .post(PostEntity.builder().id(5L).build())
                .build();
        when(commentRepository.findById(99L)).thenReturn(Optional.of(parent));

        assertThrows(CustomException.class,
                () -> commentService.createComment(req, user.getEmail()));
    }

    @Test
    void createComment_withParent_shouldSaveReply() {
        CommentRequest req = new CommentRequest(2L, "Reply", 3L);
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        CommentEntity parent = CommentEntity.builder()
                .id(3L)
                .post(post)
                .user(user)
                .comment("Parent")
                .build();
        when(commentRepository.findById(3L)).thenReturn(Optional.of(parent));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(req, user.getEmail());

        assertThat(result).isEqualTo(commentDto);
        verify(postRepository).incrementCommentCount(post.getId());
        verify(eventPublisher).publishEvent(any(NotificationEvent.class));
    }

    @Test
    void createComment_parentNotFound_shouldThrow() {
        CommentRequest req = new CommentRequest(2L, "Reply", 99L);
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class,
                () -> commentService.createComment(req, user.getEmail()));
    }

    @Test
    void updateComment_success() {
        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);
        when(commentMapper.toDto(any(CommentEntity.class))).thenReturn(commentDto);

        CommentDto res = commentService.updateComment(3L, "Updated", user.getEmail());

        assertThat(comment.getComment()).isEqualTo("Updated");
        assertThat(res).isEqualTo(commentDto);
    }

    @Test
    void updateComment_notFound_shouldThrow() {
        when(commentRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(CustomException.class,
                () -> commentService.updateComment(3L, "Updated", user.getEmail()));
    }

    @Test
    void updateComment_notOwner_shouldThrow() {
        UserEntity other = new UserEntity(); other.setId(99L);
        comment.setUser(other);
        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);

        assertThrows(CustomException.class,
                () -> commentService.updateComment(3L, "Updated", user.getEmail()));
    }

    @Test
    void updateComment_nullOrEmptyContent_shouldRetainOld() {
        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);
        when(commentMapper.toDto(any(CommentEntity.class))).thenReturn(commentDto);

        CommentDto res1 = commentService.updateComment(3L, null, user.getEmail());
        CommentDto res2 = commentService.updateComment(3L, "", user.getEmail());

        assertThat(comment.getComment()).isEqualTo("Test Comment");
        assertThat(res1).isEqualTo(commentDto);
        assertThat(res2).isEqualTo(commentDto);
    }

    @Test
    void getCommentsByPostId_sortLatestAndLiked() {
        Pageable pg = PageRequest.of(0,1);
        Page<CommentEntity> page = new PageImpl<>(Collections.singletonList(comment));

        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(commentRepository.findAll(any(Specification.class), eq(pg))).thenReturn(page);

        when(likeRepository.findCommentIdsLikedByUser(eq(user.getId()), anyList()))
                .thenReturn(Set.of(3L));

        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        Page<CommentDto> res = commentService.getCommentsByPostId(2L, "latest", pg, user.getEmail());

        assertThat(res.getContent().get(0).isLikedByUser()).isTrue();
        verify(likeRepository).findCommentIdsLikedByUser(eq(user.getId()), anyList());
    }

    @Test
    void getCommentsByPostId_sortPopular() {
        Pageable pg = PageRequest.of(0,1);
        Page<CommentEntity> page = new PageImpl<>(Collections.singletonList(comment));
        when(commentRepository.findAll(any(Specification.class), eq(pg))).thenReturn(page);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        Page<CommentDto> res = commentService.getCommentsByPostId(2L, "popular", pg, null);

        assertThat(res).isNotEmpty();
        verifyNoInteractions(likeRepository);
    }

    @Test
    void getCommentsByPostId_noSort() {
        Pageable pg = PageRequest.of(0,1);
        Page<CommentEntity> page = new PageImpl<>(Collections.singletonList(comment));
        when(commentRepository.findAll(any(Specification.class), eq(pg))).thenReturn(page);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        Page<CommentDto> res = commentService.getCommentsByPostId(2L, "random", pg, null);
        assertThat(res).isNotEmpty();
    }

    @Test
    void getReplies_mappingAndLiked() {
        Pageable pg = PageRequest.of(0,1);
        Page<CommentEntity> page = new PageImpl<>(Collections.singletonList(comment));

        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(commentRepository.findByParentCommentId(3L, pg)).thenReturn(page);

        when(likeRepository.findCommentIdsLikedByUser(eq(user.getId()), anyList()))
                .thenReturn(Collections.emptySet());

        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        Page<CommentDto> res = commentService.getReplies(3L, pg, user.getEmail());

        assertThat(res.getContent().get(0).isLikedByUser()).isFalse();
        verify(likeRepository).findCommentIdsLikedByUser(eq(user.getId()), anyList());
    }

    @Test
    void getReplies_noUsername_shouldNotCheckLikes() {
        Pageable pg = PageRequest.of(0,1);
        Page<CommentEntity> page = new PageImpl<>(Collections.singletonList(comment));
        when(commentRepository.findByParentCommentId(3L, pg)).thenReturn(page);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        Page<CommentDto> res = commentService.getReplies(3L, pg, null);

        assertThat(res).isNotEmpty();
        assertThat(res.getContent().get(0).isLikedByUser()).isFalse();
        verifyNoInteractions(likeRepository);
    }


    @Test
    void deleteComment_ownerDeletes() {
        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);

        commentService.deleteComment(3L, user.getEmail());

        verify(commentRepository).delete(comment);
        verify(postRepository).decrementCommentCount(post.getId());
        verify(notificationService).decrementNotification(user.getId(), post.getId(), NotificationType.COMMENT);
    }

    @Test
    void deleteComment_notFoundOrNotOwner_shouldThrow() {
        when(commentRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> commentService.deleteComment(3L, user.getEmail()));

        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));
        UserEntity other = new UserEntity(); other.setId(99L);
        comment.setUser(other);
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);

        assertThrows(CustomException.class, () -> commentService.deleteComment(3L, user.getEmail()));

        verify(postRepository, never()).decrementCommentCount(anyLong());
    }
}