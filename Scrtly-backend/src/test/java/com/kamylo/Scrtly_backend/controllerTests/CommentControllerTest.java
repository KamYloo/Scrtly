package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.comment.service.CommentService;
import com.kamylo.Scrtly_backend.comment.web.controller.CommentController;
import com.kamylo.Scrtly_backend.comment.web.dto.CommentDto;
import com.kamylo.Scrtly_backend.comment.web.dto.request.CommentRequest;
import com.kamylo.Scrtly_backend.comment.web.dto.request.CommentUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController controller;

    @Mock
    private Principal principal;

    @BeforeEach
    void setUp() {
    }

    private CommentDto sampleComment(Long id, String text) {
        return CommentDto.builder()
                .id(id)
                .comment(text)
                .creationDate(LocalDateTime.now())
                .likeCount(0)
                .likedByUser(false)
                .parentCommentId(null)
                .build();
    }

    @Test
    void createComment_returnsCreated_andCallsService() {
        CommentRequest req = new CommentRequest();
        req.setPostId(123L);
        req.setComment("Nice post");
        req.setParentCommentId(null);

        when(principal.getName()).thenReturn("user1");
        CommentDto dto = sampleComment(1L, "Nice post");
        when(commentService.createComment(eq(req), eq("user1"))).thenReturn(dto);

        var resp = controller.createComment(req, principal);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(commentService, times(1)).createComment(req, "user1");
    }

    @Test
    void getComments_passesPageable_andUsername_andReturnsOk() {
        Long postId = 10L;
        when(principal.getName()).thenReturn("viewer");
        CommentDto dto = sampleComment(2L, "c");
        when(commentService.getCommentsByPostId(eq(postId), eq("all"), any(Pageable.class), eq("viewer")))
                .thenReturn(new PageImpl<>(List.of(dto)));

        var resp = controller.getComments(postId, "all", 0, 9, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(commentService).getCommentsByPostId(eq(postId), eq("all"), captor.capture(), eq("viewer"));
        Pageable p = captor.getValue();
        assertEquals(0, p.getPageNumber());
        assertEquals(9, p.getPageSize());
    }

    @Test
    void getReplies_passesPageable_andReturnsOk_whenPrincipalNull() {
        Long parentId = 20L;
        CommentDto dto = sampleComment(3L, "reply");
        when(commentService.getReplies(eq(parentId), any(Pageable.class), isNull()))
                .thenReturn(new PageImpl<>(List.of(dto)));

        var resp = controller.getReplies(parentId, 1, 5, null);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(commentService).getReplies(eq(parentId), captor.capture(), isNull());
        Pageable p = captor.getValue();
        assertEquals(1, p.getPageNumber());
        assertEquals(5, p.getPageSize());
    }

    @Test
    void updateComment_callsService_andReturnsOk() {
        Long commentId = 33L;
        CommentUpdateRequest upd = new CommentUpdateRequest();
        upd.setContent("updated text");
        when(principal.getName()).thenReturn("editor");
        CommentDto dto = sampleComment(commentId, "updated text");
        when(commentService.updateComment(eq(commentId), eq("updated text"), eq("editor"))).thenReturn(dto);

        var resp = controller.updateComment(commentId, upd, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(commentService).updateComment(commentId, "updated text", "editor");
    }

    @Test
    void deleteComment_callsService_andReturnsOkWithId() {
        Long commentId = 55L;
        when(principal.getName()).thenReturn("deleter");
        var resp = controller.deleteComment(commentId, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(commentId, resp.getBody());
        verify(commentService).deleteComment(commentId, "deleter");
    }
}
//