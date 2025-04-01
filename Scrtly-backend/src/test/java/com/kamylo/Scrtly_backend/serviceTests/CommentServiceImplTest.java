package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.dto.CommentDto;
import com.kamylo.Scrtly_backend.entity.CommentEntity;
import com.kamylo.Scrtly_backend.entity.PostEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.events.NotificationEvent;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.CommentRepository;
import com.kamylo.Scrtly_backend.repository.PostRepository;
import com.kamylo.Scrtly_backend.dto.request.CommentRequest;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.service.impl.CommentServiceImpl;
import com.kamylo.Scrtly_backend.utils.UserLikeChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private Mapper<CommentEntity, CommentDto> commentMapper;

    @Mock
    private UserLikeChecker userLikeChecker;

    @Mock
    private ApplicationEventPublisher eventPublisher;

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
        post.setId(1L);
        post.setUser(user);

        comment = new CommentEntity();
        comment.setId(1L);
        comment.setUser(user);
        comment.setPost(post);
        comment.setComment("Test Comment");

        commentDto = new CommentDto();
        commentDto.setComment("Test Comment");
    }

    @Test
    void createComment_shouldSaveCommentAndPublishEvent() {
        CommentRequest request = new CommentRequest(1L, "New Comment");

        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);
        when(commentMapper.mapTo(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(request, user.getEmail());

        assertNotNull(result);
        assertEquals("Test Comment", result.getComment());
        verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class));
    }

    @Test
    void createComment_shouldThrowExceptionWhenPostNotFound() {
        CommentRequest request = new CommentRequest(1L, "New Comment");

        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> commentService.createComment(request, user.getEmail()));
    }

    @Test
    void updateComment_shouldUpdateCommentContent() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);
        when(commentMapper.mapTo(any(CommentEntity.class))).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(1L, "Updated Comment", user.getEmail());

        assertNotNull(result);
        assertEquals("Test Comment", result.getComment());
    }

    @Test
    void updateComment_shouldThrowExceptionWhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> commentService.updateComment(1L, "Updated Comment", user.getEmail()));
    }

    @Test
    void updateComment_shouldThrowExceptionWhenNotOwner() {
        UserEntity anotherUser = new UserEntity();
        anotherUser.setId(2L);

        comment.setUser(anotherUser);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);

        assertThrows(CustomException.class, () -> commentService.updateComment(1L, "Updated Comment", user.getEmail()));
    }

    @Test
    void updateComment_shouldUpdateContentWhenNotEmpty() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);
        when(commentMapper.mapTo(any(CommentEntity.class))).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(1L, "Updated Comment", user.getEmail());

        assertNotNull(result);
        verify(commentRepository, times(1)).save(comment);
        assertEquals("Updated Comment", comment.getComment());
    }

    @Test
    void updateComment_shouldNotUpdateContentWhenNull() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);
        when(commentMapper.mapTo(any(CommentEntity.class))).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(1L, null, user.getEmail());

        assertNotNull(result);
        verify(commentRepository, times(1)).save(comment);
        assertEquals("Test Comment", comment.getComment());
    }

    @Test
    void updateComment_shouldNotUpdateContentWhenEmpty() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);
        when(commentMapper.mapTo(any(CommentEntity.class))).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(1L, "", user.getEmail());

        assertNotNull(result);
        verify(commentRepository, times(1)).save(comment);
        assertEquals("Test Comment", comment.getComment());
    }

    @Test
    void getCommentsByPostId_shouldApplyLatestSorting() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentEntity> page = new PageImpl<>(Collections.singletonList(comment));

        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(commentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(commentMapper.mapTo(any(CommentEntity.class))).thenReturn(commentDto);

        Page<CommentDto> result = commentService.getCommentsByPostId(1L, "latest", pageable, user.getEmail());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(commentRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getCommentsByPostId_shouldApplyPopularSorting() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentEntity> page = new PageImpl<>(Collections.singletonList(comment));

        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(commentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(commentMapper.mapTo(any(CommentEntity.class))).thenReturn(commentDto);

        Page<CommentDto> result = commentService.getCommentsByPostId(1L, "popular", pageable, user.getEmail());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(commentRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getCommentsByPostId_shouldNotApplySortingForOtherValues() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentEntity> page = new PageImpl<>(Collections.singletonList(comment));

        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(commentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(commentMapper.mapTo(any(CommentEntity.class))).thenReturn(commentDto);

        Page<CommentDto> result = commentService.getCommentsByPostId(1L, "random_value", pageable, user.getEmail());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(commentRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }


    @Test
    void deleteComment_shouldDeleteCommentIfOwner() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);

        commentService.deleteComment(1L, user.getEmail());

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_shouldThrowExceptionWhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> commentService.deleteComment(1L, user.getEmail()));
    }

    @Test
    void deleteComment_shouldThrowExceptionWhenNotOwner() {
        UserEntity anotherUser = new UserEntity();
        anotherUser.setId(2L);

        comment.setUser(anotherUser);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);

        assertThrows(CustomException.class, () -> commentService.deleteComment(1L, user.getEmail()));
    }

    @Test
    void getCommentsByPostId_shouldReturnSortedComments() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<CommentEntity> page = new PageImpl<>(Collections.singletonList(comment));

        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(commentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(commentMapper.mapTo(any(CommentEntity.class))).thenReturn(commentDto);
        when(userLikeChecker.isCommentLikedByUser(any(CommentEntity.class), eq(user.getId()))).thenReturn(true);

        Page<CommentDto> result = commentService.getCommentsByPostId(1L, "latest", pageable, user.getEmail());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }
}
