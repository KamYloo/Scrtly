package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.like.mapper.PostLikeMapper;
import com.kamylo.Scrtly_backend.like.mapper.CommentLikeMapper;
import com.kamylo.Scrtly_backend.like.web.dto.PostStatsDto;
import com.kamylo.Scrtly_backend.like.web.dto.CommentStatsDto;
import com.kamylo.Scrtly_backend.comment.domain.CommentEntity;
import com.kamylo.Scrtly_backend.like.domain.LikeEntity;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
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
import com.kamylo.Scrtly_backend.common.utils.UserLikeChecker;
import com.kamylo.Scrtly_backend.like.service.impl.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock private UserService userService;
    @Mock private LikeRepository likeRepository;
    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private PostLikeMapper postLikeMapper;
    @Mock private CommentLikeMapper commentLikeMapper;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private NotificationService notificationService;
    @Mock private UserLikeChecker userLikeChecker;

    @InjectMocks private LikeServiceImpl likeService;

    private UserEntity user;
    private PostEntity post;
    private CommentEntity comment;
    private LikeEntity existingLike;
    private PostStatsDto postDto;
    private CommentStatsDto commentDto;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(100L)
                .email("test@example.com")
                .build();

        post = PostEntity.builder()
                .id(1L)
                .user(user)
                .likes(new HashSet<>())
                .build();

        comment = CommentEntity.builder()
                .id(10L)
                .user(user)
                .likes(new HashSet<>())
                .build();

        existingLike = LikeEntity.builder()
                .id(500L)
                .user(user)
                .post(post)
                .build();

        postDto = new PostStatsDto();
        commentDto = new CommentStatsDto();

        lenient().when(userLikeChecker.isPostLikedByUser(any(), anyLong())).thenReturn(false);
        lenient().when(userLikeChecker.isCommentLikedByUser(any(), anyLong())).thenReturn(false);
    }

    @Test
    void likePost_shouldUnlike_whenAlreadyLiked() {
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(likeRepository.isLikeExistPost(user.getId(), post.getId())).thenReturn(existingLike);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(postLikeMapper.toDto(existingLike)).thenReturn(postDto);

        PostStatsDto result = likeService.likePost(post.getId(), user.getEmail());

        assertSame(postDto, result);
        verify(likeRepository).deleteById(existingLike.getId());
        verify(notificationService).decrementNotification(post.getUser().getId(), post.getId(), NotificationType.LIKE);
        verify(likeRepository, never()).save(any(LikeEntity.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void likePost_shouldLike_whenNotAlreadyLiked() {
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(likeRepository.isLikeExistPost(user.getId(), post.getId())).thenReturn(null);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        ArgumentCaptor<LikeEntity> likeCaptor = ArgumentCaptor.forClass(LikeEntity.class);
        when(likeRepository.save(likeCaptor.capture())).thenAnswer(invocation -> {
            LikeEntity arg = invocation.getArgument(0);
            arg.setId(501L);
            return arg;
        });

        when(postLikeMapper.toDto(any(LikeEntity.class))).thenReturn(postDto);

        PostStatsDto result = likeService.likePost(post.getId(), user.getEmail());

        assertSame(postDto, result);

        LikeEntity saved = likeCaptor.getValue();
        assertNotNull(saved);
        assertEquals(user, saved.getUser());
        assertEquals(post, saved.getPost());

        verify(postRepository).save(post);
        verify(eventPublisher).publishEvent(any(NotificationEvent.class));
        verify(likeRepository, never()).deleteById(anyLong());
        verify(notificationService, never()).decrementNotification(anyLong(), anyLong(), any());
    }

    @Test
    void likePost_shouldThrow_whenPostNotFound() {
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(likeRepository.isLikeExistPost(user.getId(), post.getId())).thenReturn(null);
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> likeService.likePost(post.getId(), user.getEmail()));
        assertEquals(BusinessErrorCodes.POST_NOT_FOUND, ex.getErrorCode());

        verify(likeRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void likeComment_shouldUnlike_whenAlreadyLiked() {
        LikeEntity likeForComment = LikeEntity.builder()
                .id(600L)
                .user(user)
                .comment(comment)
                .build();

        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(likeRepository.isLikeExistComment(user.getId(), comment.getId())).thenReturn(likeForComment);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(commentLikeMapper.toDto(likeForComment)).thenReturn(commentDto);

        CommentStatsDto result = likeService.likeComment(comment.getId(), user.getEmail());

        assertSame(commentDto, result);
        verify(likeRepository).deleteById(likeForComment.getId());
        verify(likeRepository, never()).save(any(LikeEntity.class));
    }

    @Test
    void likeComment_shouldLike_whenNotAlreadyLiked() {
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(likeRepository.isLikeExistComment(user.getId(), comment.getId())).thenReturn(null);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        ArgumentCaptor<LikeEntity> captor = ArgumentCaptor.forClass(LikeEntity.class);
        when(likeRepository.save(captor.capture())).thenAnswer(invocation -> {
            LikeEntity arg = invocation.getArgument(0);
            arg.setId(502L);
            return arg;
        });
        when(commentLikeMapper.toDto(any(LikeEntity.class))).thenReturn(commentDto);

        CommentStatsDto result = likeService.likeComment(comment.getId(), user.getEmail());

        assertSame(commentDto, result);
        LikeEntity saved = captor.getValue();
        assertNotNull(saved);
        assertEquals(user, saved.getUser());
        assertEquals(comment, saved.getComment());
        verify(commentRepository).save(comment);
        verify(likeRepository, never()).deleteById(anyLong());
    }

    @Test
    void likeComment_shouldThrow_whenCommentNotFound() {
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(likeRepository.isLikeExistComment(user.getId(), comment.getId())).thenReturn(null);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> likeService.likeComment(comment.getId(), user.getEmail()));
        assertEquals(BusinessErrorCodes.COMMENT_NOT_FOUND, ex.getErrorCode());

        verify(likeRepository, never()).save(any());
    }
}
