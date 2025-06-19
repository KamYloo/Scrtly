package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.dto.PostStatsDto;
import com.kamylo.Scrtly_backend.dto.CommentStatsDto;
import com.kamylo.Scrtly_backend.entity.CommentEntity;
import com.kamylo.Scrtly_backend.entity.LikeEntity;
import com.kamylo.Scrtly_backend.entity.PostEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.entity.enums.NotificationType;
import com.kamylo.Scrtly_backend.events.NotificationEvent;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.CommentRepository;
import com.kamylo.Scrtly_backend.repository.LikeRepository;
import com.kamylo.Scrtly_backend.repository.PostRepository;
import com.kamylo.Scrtly_backend.service.NotificationService;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.service.impl.LikeServiceImpl;
import com.kamylo.Scrtly_backend.utils.UserLikeChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class LikeServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private Mapper<LikeEntity, PostStatsDto> postMapper;

    @Mock
    private Mapper<LikeEntity, CommentStatsDto> commentMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserLikeChecker userLikeChecker;

    @InjectMocks
    private LikeServiceImpl likeService;

    private UserEntity user;
    private PostEntity post;
    private CommentEntity comment;
    private LikeEntity likeEntity;
    private PostStatsDto postDto;
    private CommentStatsDto commentDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new UserEntity();
        user.setId(100L);
        user.setEmail("test@example.com");

        post = new PostEntity();
        post.setId(1L);
        post.setUser(user);
        post.setLikes(new java.util.HashSet<>());

        comment = new CommentEntity();
        comment.setId(10L);
        comment.setUser(user);
        comment.setLikes(new java.util.HashSet<>());

        likeEntity = new LikeEntity();
        likeEntity.setId(500L);
        likeEntity.setUser(user);
        likeEntity.setPost(post);

        postDto = new PostStatsDto();
        commentDto = new CommentStatsDto();

        when(userLikeChecker.isPostLikedByUser(any(), anyLong())).thenReturn(false);
        when(userLikeChecker.isCommentLikedByUser(any(), anyLong())).thenReturn(false);
    }

    @Test
    void likePost_shouldUnlike_whenAlreadyLiked() {
        when(userService.findUserByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.isLikeExistPost(user.getId(), post.getId())).thenReturn(likeEntity);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postMapper.mapTo(likeEntity)).thenReturn(postDto);

        PostStatsDto result = likeService.likePost(post.getId(), "test@example.com");

        verify(likeRepository).deleteById(likeEntity.getId());
        verify(notificationService).decrementNotification(user.getId(), post.getId(), NotificationType.LIKE);
        assertEquals(postDto, result);
    }

    @Test
    void likePost_shouldLike_whenNotAlreadyLiked() {
        when(userService.findUserByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.isLikeExistPost(user.getId(), post.getId())).thenReturn(null);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likeRepository.save(any(LikeEntity.class))).thenAnswer(invocation -> {
            LikeEntity saved = invocation.getArgument(0);
            saved.setId(501L);
            return saved;
        });
        when(postMapper.mapTo(any(LikeEntity.class))).thenReturn(postDto);

        PostStatsDto result = likeService.likePost(post.getId(), "test@example.com");

        verify(likeRepository).save(any(LikeEntity.class));
        verify(postRepository).save(post);
        verify(eventPublisher).publishEvent(any(NotificationEvent.class));
        assertEquals(postDto, result);
    }

    @Test
    void likePost_shouldThrowException_whenPostNotFound() {
        when(userService.findUserByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.isLikeExistPost(user.getId(), post.getId())).thenReturn(null);
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> likeService.likePost(post.getId(), "test@example.com")
        );
        assertEquals(BusinessErrorCodes.POST_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void likeComment_shouldUnlike_whenAlreadyLiked() {
        likeEntity.setComment(comment);
        likeEntity.setPost(null);

        when(userService.findUserByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.isLikeExistComment(user.getId(), comment.getId())).thenReturn(likeEntity);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(commentMapper.mapTo(likeEntity)).thenReturn(commentDto);

        CommentStatsDto result = likeService.likeComment(comment.getId(), "test@example.com");

        verify(likeRepository).deleteById(likeEntity.getId());
        assertEquals(commentDto, result);
    }

    @Test
    void likeComment_shouldLike_whenNotAlreadyLiked() {
        when(userService.findUserByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.isLikeExistComment(user.getId(), comment.getId())).thenReturn(null);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(likeRepository.save(any(LikeEntity.class))).thenAnswer(invocation -> {
            LikeEntity saved = invocation.getArgument(0);
            saved.setId(502L);
            return saved;
        });
        when(commentMapper.mapTo(any(LikeEntity.class))).thenReturn(commentDto);

        CommentStatsDto result = likeService.likeComment(comment.getId(), "test@example.com");

        verify(likeRepository).save(any(LikeEntity.class));
        verify(commentRepository).save(comment);
        assertEquals(commentDto, result);
    }

    @Test
    void likeComment_shouldThrowException_whenCommentNotFound() {
        when(userService.findUserByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.isLikeExistComment(user.getId(), comment.getId())).thenReturn(null);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> likeService.likeComment(comment.getId(), "test@example.com")
        );
        assertEquals(BusinessErrorCodes.COMMENT_NOT_FOUND, ex.getErrorCode());
    }
}
