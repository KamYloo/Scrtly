package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.like.repository.LikeRepository;
import com.kamylo.Scrtly_backend.post.mapper.PostMapper;
import com.kamylo.Scrtly_backend.post.web.dto.PostDto;
import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.post.repository.PostRepository;
import com.kamylo.Scrtly_backend.notification.service.NotificationService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.common.service.impl.FileServiceImpl;
import com.kamylo.Scrtly_backend.post.service.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock private PostRepository postRepository;
    @Mock private UserService userService;
    @Mock private FileServiceImpl fileService;
    @Mock private PostMapper postMapper;
    @Mock private LikeRepository likeRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks private PostServiceImpl postService;

    private UserEntity createUser(Long id) {
        UserEntity u = new UserEntity();
        u.setId(id);
        u.setEmail("user" + id + "@example.com");
        return u;
    }

    private PostEntity createPostEntity(Long id, UserEntity user, String description, String image) {
        return PostEntity.builder()
                .id(id)
                .user(user)
                .description(description)
                .image(image)
                .likeCount(0)
                .commentCount(0)
                .build();
    }

    private PostDto createPostDto(Long id, String description, String image) {
        PostDto dto = new PostDto();
        dto.setId(id);
        dto.setDescription(description);
        dto.setImage(image);
        return dto;
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void createPost_shouldCreatePostWithImage() {
        String username = "user@example.com";
        String description = "New post";
        MultipartFile file = mock(MultipartFile.class);

        UserEntity user = createUser(1L);
        PostEntity savedEntity = createPostEntity(1L, user, description, "image/path");
        PostDto expectedDto = createPostDto(1L, description, "image/path");

        when(userService.findUserByEmail(username)).thenReturn(user);
        when(file.isEmpty()).thenReturn(false);
        when(fileService.saveFile(file, "postImages/")).thenReturn("image/path");
        when(postRepository.save(any(PostEntity.class))).thenReturn(savedEntity);
        when(postMapper.toDto(any(PostEntity.class))).thenReturn(expectedDto);

        PostDto result = postService.createPost(username, description, file);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals("image/path", result.getImage());

        verify(userService).findUserByEmail(username);
        verify(fileService).saveFile(file, "postImages/");
        verify(postRepository).save(any(PostEntity.class));
        verify(postMapper).toDto(savedEntity);
    }

    @Test
    void createPost_shouldCreatePostWithoutImage() {
        String username = "user@example.com";
        String description = "No image post";
        MultipartFile file = mock(MultipartFile.class);

        UserEntity user = createUser(1L);
        PostEntity savedEntity = createPostEntity(2L, user, description, null);
        PostDto expectedDto = createPostDto(2L, description, null);

        when(userService.findUserByEmail(username)).thenReturn(user);
        when(file.isEmpty()).thenReturn(true);
        when(postRepository.save(any(PostEntity.class))).thenReturn(savedEntity);
        when(postMapper.toDto(any(PostEntity.class))).thenReturn(expectedDto);

        PostDto result = postService.createPost(username, description, file);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertNull(result.getImage());

        verify(userService).findUserByEmail(username);
        verify(fileService, never()).saveFile(any(), anyString());
        verify(postRepository).save(any(PostEntity.class));
    }

    @Test
    void updatePost_shouldUpdatePost_imageAndDescription() {
        Long postId = 1L;
        String username = "user@example.com";
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);

        UserEntity user = createUser(1L);
        user.setEmail(username);
        PostEntity postEntity = createPostEntity(postId, user, "Old description", "old/path");

        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(fileService.updateFile(file, "old/path", "postImages/")).thenReturn("new/path");
        when(postRepository.save(any(PostEntity.class))).thenReturn(postEntity);

        PostDto expectedDto = createPostDto(postId, "Updated description", "new/path");
        when(postMapper.toDto(any(PostEntity.class))).thenReturn(expectedDto);

        PostDto result = postService.updatePost(postId, username, file, "Updated description");

        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
        assertEquals("new/path", result.getImage());

        verify(postRepository).findById(postId);
        verify(userService).findUserByEmail(username);
        verify(fileService).updateFile(file, "old/path", "postImages/");
        verify(postRepository).save(postEntity);
    }

    @Test
    void updatePost_shouldThrowExceptionWhenPostNotFound() {
        Long postId = 1L;
        String username = "user@example.com";
        MultipartFile file = mock(MultipartFile.class);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.updatePost(postId, username, file, "desc"));

        assertEquals(BusinessErrorCodes.POST_NOT_FOUND, ex.getErrorCode());
        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(userService, fileService, postRepository);
    }

    @Test
    void updatePost_shouldThrowExceptionWhenUserNotOwner() {
        Long postId = 1L;
        String username = "user@example.com";

        UserEntity owner = createUser(2L);
        PostEntity post = createPostEntity(postId, owner, null, null);

        UserEntity caller = createUser(1L); // Different ID
        caller.setEmail(username);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserByEmail(username)).thenReturn(caller);

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.updatePost(postId, username, null, "New description"));

        assertEquals(BusinessErrorCodes.POST_MISMATCH, ex.getErrorCode());
        verify(postRepository).findById(postId);
        verify(userService).findUserByEmail(username);
    }

    @Test
    void updatePost_shouldUpdateOnlyDescription_whenFileNull() {
        Long postId = 1L;
        String username = "user@example.com";

        UserEntity user = createUser(1L);
        user.setEmail(username);
        PostEntity post = createPostEntity(postId, user, "Old description", "old/path");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(postRepository.save(any(PostEntity.class))).thenReturn(post);

        PostDto expected = createPostDto(postId, "New description", "old/path");
        when(postMapper.toDto(any(PostEntity.class))).thenReturn(expected);

        PostDto result = postService.updatePost(postId, username, null, "New description");

        assertNotNull(result);
        assertEquals("New description", result.getDescription());
        assertEquals("old/path", result.getImage());

        verify(fileService, never()).updateFile(any(), any(), anyString());
    }

    @Test
    void deletePost_shouldDeletePost() {
        Long postId = 1L;
        String username = "user@example.com";
        UserEntity user = createUser(1L);
        user.setEmail(username);
        PostEntity postEntity = createPostEntity(postId, user, null, "image/path");

        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(userService.findUserByEmail(username)).thenReturn(user);

        postService.deletePost(postId, username);

        verify(fileService).deleteFile("image/path");
        verify(notificationService).deleteNotificationsByPost(postEntity);
        verify(postRepository).deleteById(postId);
    }

    @Test
    void getPostsByUser_shouldReturnUserPosts() {
        String nickName = "user123";
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        Pageable pageable = PageRequest.of(0, 10);

        PostEntity postEntity = createPostEntity(1L, null, "Test post", null);
        PostDto postDto = createPostDto(1L, "Test post", null);

        when(userService.findUserByNickname(nickName)).thenReturn(userDto);
        when(postRepository.findByUserId(eq(userDto.getId()), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.singletonList(postEntity)));
        when(postMapper.toDto(any(PostEntity.class))).thenReturn(postDto);

        Page<PostDto> result = postService.getPostsByUser(nickName, pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());
        assertEquals("Test post", result.getContent().get(0).getDescription());
    }

    @Test
    void getPosts_shouldReturnAllPosts_whenUserProvided_marksLikedByUser() {
        String username = "user@example.com";
        Pageable pageable = PageRequest.of(0, 10);
        UserEntity user = createUser(1L);

        PostEntity postEntity = createPostEntity(100L, null, "Test post", null);
        PostDto postDto = createPostDto(100L, "Test post", null);

        when(postRepository.findAll(ArgumentMatchers.<Specification<PostEntity>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.singletonList(postEntity)));
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(likeRepository.findPostIdsLikedByUser(eq(user.getId()), anyList()))
                .thenReturn(Set.of(100L));
        when(postMapper.toDto(any(PostEntity.class))).thenReturn(postDto);

        Page<PostDto> result = postService.getPosts(pageable, username, null, null);

        assertFalse(result.isEmpty());
        verify(likeRepository).findPostIdsLikedByUser(eq(user.getId()), anyList());
        assertTrue(result.getContent().get(0).isLikedByUser());
    }

    @Test
    void getPosts_shouldReturnAllPosts_whenUserProvided_butNotLiked() {
        String username = "user@example.com";
        Pageable pageable = PageRequest.of(0, 10);
        UserEntity user = createUser(1L);

        PostEntity postEntity = createPostEntity(100L, null, "Test post", null);
        PostDto postDto = createPostDto(100L, "Test post", null);

        when(postRepository.findAll(ArgumentMatchers.<Specification<PostEntity>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.singletonList(postEntity)));

        when(userService.findUserByEmail(username)).thenReturn(user);

        when(likeRepository.findPostIdsLikedByUser(eq(user.getId()), anyList()))
                .thenReturn(Collections.emptySet());

        when(postMapper.toDto(any(PostEntity.class))).thenReturn(postDto);

        Page<PostDto> result = postService.getPosts(pageable, username, null, null);

        assertFalse(result.isEmpty());
        verify(likeRepository).findPostIdsLikedByUser(eq(user.getId()), anyList());
        assertFalse(result.getContent().get(0).isLikedByUser());
    }

    @Test
    void getPosts_shouldReturnAllPosts_whenNoUsername_skipLikeChecking() {
        Pageable pageable = PageRequest.of(0, 10);
        PostEntity postEntity = createPostEntity(2L, null, "Public post", null);
        PostDto postDto = createPostDto(2L, "Public post", null);

        when(postRepository.findAll(ArgumentMatchers.<Specification<PostEntity>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.singletonList(postEntity)));
        when(postMapper.toDto(any(PostEntity.class))).thenReturn(postDto);

        Page<PostDto> result = postService.getPosts(pageable, null, null, null);

        assertFalse(result.isEmpty());

        verify(userService, never()).findUserByEmail(anyString());
        verify(likeRepository, never()).findPostIdsLikedByUser(anyLong(), anyList());
        assertFalse(result.getContent().get(0).isLikedByUser());
    }

    @Test
    void getPosts_shouldReturnEmptyPage_whenNoPostsFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(postRepository.findAll(ArgumentMatchers.<Specification<PostEntity>>any(), eq(pageable)))
                .thenReturn(Page.empty());

        Page<PostDto> result = postService.getPosts(pageable, "someUser", null, null);

        assertTrue(result.isEmpty());
        verify(userService, never()).findUserByEmail(anyString());
        verify(likeRepository, never()).findPostIdsLikedByUser(anyLong(), anyList());
    }
}