package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.post.service.PostService;
import com.kamylo.Scrtly_backend.post.web.dto.PostDto;
import com.kamylo.Scrtly_backend.post.web.dto.request.PostCreateRequest;
import com.kamylo.Scrtly_backend.post.web.dto.request.PostUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private com.kamylo.Scrtly_backend.post.web.controller.PostController controller;

    @Mock
    private Principal principal;

    private PostDto samplePost(Long id, String desc) {
        return PostDto.builder()
                .id(id)
                .image("img.png")
                .description(desc)
                .creationDate(LocalDateTime.now())
                .likeCount(0)
                .commentCount(0)
                .likedByUser(false)
                .build();
    }

    @Test
    void createPost_returnsCreated_andCallsService() {
        when(principal.getName()).thenReturn("author");
        PostCreateRequest req = PostCreateRequest.builder()
                .description("hello")
                .file(mock(MultipartFile.class))
                .build();

        PostDto dto = samplePost(1L, "hello");
        when(postService.createPost(eq("author"), eq("hello"), any(MultipartFile.class))).thenReturn(dto);

        var resp = controller.createPost(req, principal);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(postService).createPost("author", "hello", req.getFile());
    }

    @Test
    void updatePost_returnsOk_andCallsService() {
        Long postId = 5L;
        when(principal.getName()).thenReturn("editor");
        PostUpdateRequest req = PostUpdateRequest.builder()
                .description("updated")
                .file(mock(MultipartFile.class))
                .build();

        PostDto dto = samplePost(postId, "updated");
        when(postService.updatePost(eq(postId), eq("editor"), any(MultipartFile.class), eq("updated"))).thenReturn(dto);

        var resp = controller.updatePost(postId, req, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(postService).updatePost(postId, "editor", req.getFile(), "updated");
    }

    @Test
    void getAllPosts_passesPageable_andReturnsOk() {
        int page = 2;
        int size = 3;
        Sort.Direction dir = Sort.Direction.ASC;
        PostDto dto = samplePost(7L, "p");

        when(principal.getName()).thenReturn("author");

        doReturn(new PageImpl<>(List.of(dto)))
                .when(postService)
                .getPosts(any(Pageable.class), eq("author"), isNull(), isNull());

        var resp = controller.getAllPosts(null, null, dir, page, size, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(postService).getPosts(captor.capture(), eq("author"), isNull(), isNull());
        verify(principal).getName();
        Pageable p = captor.getValue();
        assertEquals(page, p.getPageNumber());
        assertEquals(size, p.getPageSize());
        assertNotNull(p.getSort().getOrderFor("creationDate"));
    }

    @Test
    void getAllPostsByUser_callsService_andReturnsOk() {
        String nick = "nick1";
        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "creationDate");
        PostDto dto = samplePost(9L, "byUser");
        when(postService.getPostsByUser(eq(nick), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(dto)));

        var resp = controller.getAllPostsByUser(nick, pageable);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(postService).getPostsByUser(eq(nick), captor.capture());
        Pageable p = captor.getValue();
        assertEquals(0, p.getPageNumber());
        assertEquals(10, p.getPageSize());
    }

    @Test
    void deletePost_callsService_andReturnsOkWithId() {
        Long id = 42L;
        when(principal.getName()).thenReturn("deleter");

        var resp = controller.deletePost(id, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(id, resp.getBody());
        verify(postService).deletePost(id, "deleter");
    }
}
