package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.like.service.LikeService;
import com.kamylo.Scrtly_backend.like.service.SongLikeService;
import com.kamylo.Scrtly_backend.like.web.controller.LikeController;
import com.kamylo.Scrtly_backend.like.web.dto.CommentStatsDto;
import com.kamylo.Scrtly_backend.like.web.dto.PostStatsDto;
import com.kamylo.Scrtly_backend.like.web.dto.SongLikeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @Mock
    private SongLikeService songLikeService;

    @InjectMocks
    private LikeController controller;

    @Mock
    private Principal principal;

    @Test
    void likePost_callsService_andReturnsOk() {
        Long postId = 10L;
        when(principal.getName()).thenReturn("userX");
        PostStatsDto dto = mock(PostStatsDto.class);
        when(likeService.likePost(postId, "userX")).thenReturn(dto);

        ResponseEntity<PostStatsDto> resp = controller.likePost(postId, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(likeService).likePost(postId, "userX");
    }

    @Test
    void likeComment_callsService_andReturnsOk() {
        Long commentId = 20L;
        when(principal.getName()).thenReturn("userY");
        CommentStatsDto dto = mock(CommentStatsDto.class);
        when(likeService.likeComment(commentId, "userY")).thenReturn(dto);

        ResponseEntity<CommentStatsDto> resp = controller.likeComment(commentId, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(likeService).likeComment(commentId, "userY");
    }

    @Test
    void likeSong_callsService_andReturnsOk() {
        Long songId = 30L;
        when(principal.getName()).thenReturn("userZ");
        SongLikeDto dto = mock(SongLikeDto.class);
        when(songLikeService.likeSong(songId, "userZ")).thenReturn(dto);

        ResponseEntity<SongLikeDto> resp = controller.likeSong(songId, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(songLikeService).likeSong(songId, "userZ");
    }
}