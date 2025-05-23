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
import com.kamylo.Scrtly_backend.service.impl.LikeServiceImpl;
import com.kamylo.Scrtly_backend.service.UserService;
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
    private Mapper<LikeEntity, PostStatsDto> likeMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LikeServiceImpl likeService;

    private UserEntity user;
    private PostEntity post;
    private CommentEntity comment;
    private LikeEntity likeEntity;
    private PostStatsDto likeDto;

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

        likeDto = new PostStatsDto();
    }


    @Test
    void likePost_shouldUnlike_whenAlreadyLiked() {
        when(userService.findUserByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.isLikeExistPost(user.getId(), post.getId())).thenReturn(likeEntity);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(likeMapper.mapTo(likeEntity)).thenReturn(likeDto);

        PostStatsDto result = likeService.likePost(post.getId(), "test@example.com");

        verify(likeRepository, times(1)).deleteById(likeEntity.getId());
        verify(notificationService, times(1))
                .decrementNotification(post.getUser().getId(), post.getId(), NotificationType.LIKE);
        assertNotNull(result);
        assertEquals(likeDto, result);
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
        when(likeMapper.mapTo(any(LikeEntity.class))).thenReturn(likeDto);

        PostStatsDto result = likeService.likePost(post.getId(), "test@example.com");

        verify(likeRepository, times(1)).save(any(LikeEntity.class));
        verify(postRepository, times(1)).save(post);
        verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class));
        assertNotNull(result);
        assertEquals(likeDto, result);
    }

    @Test
    void likePost_shouldThrowException_whenPostNotFound() {
        when(userService.findUserByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.isLikeExistPost(user.getId(), post.getId())).thenReturn(null);
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () ->
                likeService.likePost(post.getId(), "test@example.com")
        );
        assertEquals(BusinessErrorCodes.POST_NOT_FOUND, ex.getErrorCode());
    }


    @Test
    void likeComment_shouldUnlike_whenAlreadyLiked() {
        likeEntity.setComment(comment);
        likeEntity.setPost(null);

        when(userService.findUserByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.isLikeExistComment(user.getId(), comment.getId())).thenReturn(likeEntity);
        when(likeMapper.mapTo(likeEntity)).thenReturn(likeDto);

        CommentStatsDto result = likeService.likeComment(comment.getId(), "test@example.com");

        verify(likeRepository, times(1)).deleteById(likeEntity.getId());
        assertNotNull(result);
        assertEquals(likeDto, result);
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
        when(likeMapper.mapTo(any(LikeEntity.class))).thenReturn(likeDto);

        CommentStatsDto result = likeService.likeComment(comment.getId(), "test@example.com");

        verify(likeRepository, times(1)).save(any(LikeEntity.class));
        verify(commentRepository, times(1)).save(comment);
        assertNotNull(result);
        assertEquals(likeDto, result);
    }

    @Test
    void likeComment_shouldThrowException_whenCommentNotFound() {
        when(userService.findUserByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.isLikeExistComment(user.getId(), comment.getId())).thenReturn(null);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () ->
                likeService.likeComment(comment.getId(), "test@example.com")
        );
        assertEquals(BusinessErrorCodes.COMMENT_NOT_FOUND, ex.getErrorCode());
    }
}
