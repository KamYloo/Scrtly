package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.dto.PostDto;
import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.entity.PostEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.PostRepository;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.service.impl.FileServiceImpl;
import com.kamylo.Scrtly_backend.service.impl.PostServiceImpl;
import com.kamylo.Scrtly_backend.utils.UserLikeChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private FileServiceImpl fileService;

    @Mock
    private Mapper<PostEntity, PostDto> postMapper;

    @Mock
    private UserLikeChecker userLikeChecker;

    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPost_shouldCreatePostWithImage() {
        String username = "user@example.com";
        String description = "New post";
        MultipartFile file = mock(MultipartFile.class);
        UserEntity user = new UserEntity();
        user.setId(1L);

        PostEntity postEntity = PostEntity.builder()
                .user(user)
                .description(description)
                .image("image/path")
                .build();

        PostDto expectedDto = new PostDto();
        expectedDto.setDescription(description);
        expectedDto.setImage("image/path");

        when(userService.findUserByEmail(username)).thenReturn(user);
        when(fileService.saveFile(any(), anyString())).thenReturn("image/path");
        when(postRepository.save(any(PostEntity.class))).thenReturn(postEntity);
        when(postMapper.mapTo(any(PostEntity.class))).thenReturn(expectedDto);

        PostDto result = postService.createPost(username, description, file);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals("image/path", result.getImage());
    }

    @Test
    void createPost_shouldCreatePostWithoutImage() {
        String username = "user@example.com";
        String description = "No image post";
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        UserEntity user = new UserEntity();
        user.setId(1L);

        PostEntity postEntity = PostEntity.builder()
                .user(user)
                .description(description)
                .image(null)
                .build();

        PostDto expectedDto = new PostDto();
        expectedDto.setDescription(description);
        expectedDto.setImage(null);

        when(userService.findUserByEmail(username)).thenReturn(user);
        when(postRepository.save(any(PostEntity.class))).thenReturn(postEntity);
        when(postMapper.mapTo(any(PostEntity.class))).thenReturn(expectedDto);

        PostDto result = postService.createPost(username, description, file);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertNull(result.getImage());
    }

    @Test
    void updatePost_shouldUpdatePost() {
        Long postId = 1L;
        String username = "user@example.com";
        String newDescription = "Updated description";
        MultipartFile file = mock(MultipartFile.class);
        UserEntity user = new UserEntity();
        user.setId(1L);

        PostEntity postEntity = PostEntity.builder()
                .id(postId)
                .user(user)
                .description("Old description")
                .image("old/path")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(file.isEmpty()).thenReturn(false);
        when(fileService.updateFile(any(), any(), anyString())).thenReturn("new/path");
        when(postRepository.save(any(PostEntity.class))).thenReturn(postEntity);

        PostDto expectedDto = new PostDto();
        expectedDto.setDescription(newDescription);
        expectedDto.setImage("new/path");

        when(postMapper.mapTo(any(PostEntity.class))).thenReturn(expectedDto);

        PostDto result = postService.updatePost(postId, username, file, newDescription);

        assertNotNull(result);
        assertEquals(newDescription, result.getDescription());
        assertEquals("new/path", result.getImage());
    }

    @Test
    void updatePost_shouldThrowExceptionWhenPostNotFound() {
        Long postId = 1L;
        String username = "user@example.com";
        MultipartFile file = mock(MultipartFile.class);
        String description = "Updated description";

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                postService.updatePost(postId, username, file, description)
        );

        assertEquals(BusinessErrorCodes.POST_NOT_FOUND, exception.getErrorCode());
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void updatePost_shouldThrowExceptionWhenUserNotOwner() {
        Long postId = 1L;
        String username = "user@example.com";
        UserEntity user1 = new UserEntity();
        user1.setId(1L);

        UserEntity user2 = new UserEntity();
        user2.setId(2L); // Inny użytkownik

        PostEntity post = PostEntity.builder()
                .id(postId)
                .user(user2) // Post należy do innego użytkownika
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserByEmail(username)).thenReturn(user1);

        CustomException exception = assertThrows(CustomException.class, () ->
                postService.updatePost(postId, username, null, "New description")
        );

        assertEquals(BusinessErrorCodes.POST_MISMATCH, exception.getErrorCode());
        verify(postRepository, times(1)).findById(postId);
        verify(userService, times(1)).findUserByEmail(username);
    }

    @Test
    void updatePost_shouldUpdateOnlyDescription() {
        Long postId = 1L;
        String username = "user@example.com";
        UserEntity user = new UserEntity();
        user.setId(1L);

        PostEntity post = PostEntity.builder()
                .id(postId)
                .user(user)
                .description("Old description")
                .image("old/path")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(postRepository.save(any(PostEntity.class))).thenReturn(post);

        PostDto expectedDto = new PostDto();
        expectedDto.setDescription("New description");
        expectedDto.setImage("old/path");

        when(postMapper.mapTo(any(PostEntity.class))).thenReturn(expectedDto);

        PostDto result = postService.updatePost(postId, username, null, "New description");

        assertNotNull(result);
        assertEquals("New description", result.getDescription());
        assertEquals("old/path", result.getImage()); // Obrazek nie zmienia się
    }

    @Test
    void updatePost_shouldUpdateOnlyImage() {
        Long postId = 1L;
        String username = "user@example.com";
        UserEntity user = new UserEntity();
        user.setId(1L);

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);

        PostEntity post = PostEntity.builder()
                .id(postId)
                .user(user)
                .description("Old description")
                .image("old/path")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(fileService.updateFile(file, "old/path", "postImages/")).thenReturn("new/path");
        when(postRepository.save(any(PostEntity.class))).thenReturn(post);

        PostDto expectedDto = new PostDto();
        expectedDto.setDescription("Old description");
        expectedDto.setImage("new/path");

        when(postMapper.mapTo(any(PostEntity.class))).thenReturn(expectedDto);

        PostDto result = postService.updatePost(postId, username, file, null);

        assertNotNull(result);
        assertEquals("Old description", result.getDescription());
        assertEquals("new/path", result.getImage());
    }

    @Test
    void updatePost_shouldNotUpdateImageWhenFileIsNull() {
        Long postId = 1L;
        String username = "user@example.com";
        UserEntity user = new UserEntity();
        user.setId(1L);

        PostEntity post = PostEntity.builder()
                .id(postId)
                .user(user)
                .description("Old description")
                .image("old/path")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(postRepository.save(any(PostEntity.class))).thenReturn(post);

        PostDto expectedDto = new PostDto();
        expectedDto.setDescription("Updated description");
        expectedDto.setImage("old/path"); // Nie zmieniamy obrazka

        when(postMapper.mapTo(any(PostEntity.class))).thenReturn(expectedDto);

        PostDto result = postService.updatePost(postId, username, null, "Updated description");

        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
        assertEquals("old/path", result.getImage()); // Sprawdzamy, że obrazek nie został zmieniony
    }

    @Test
    void updatePost_shouldNotUpdateImageWhenFileIsEmpty() {
        Long postId = 1L;
        String username = "user@example.com";
        UserEntity user = new UserEntity();
        user.setId(1L);

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true); // Plik jest pusty

        PostEntity post = PostEntity.builder()
                .id(postId)
                .user(user)
                .description("Old description")
                .image("old/path")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(postRepository.save(any(PostEntity.class))).thenReturn(post);

        PostDto expectedDto = new PostDto();
        expectedDto.setDescription("Updated description");
        expectedDto.setImage("old/path"); // Obrazek nie powinien się zmienić

        when(postMapper.mapTo(any(PostEntity.class))).thenReturn(expectedDto);

        PostDto result = postService.updatePost(postId, username, file, "Updated description");

        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
        assertEquals("old/path", result.getImage()); // Sprawdzamy, że obrazek nie został zmieniony
    }

    @Test
    void updatePost_shouldNotUpdateDescriptionWhenNull() {
        Long postId = 1L;
        String username = "user@example.com";
        UserEntity user = new UserEntity();
        user.setId(1L);

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(fileService.updateFile(file, "old/path", "postImages/")).thenReturn("new/path");

        PostEntity post = PostEntity.builder()
                .id(postId)
                .user(user)
                .description("Old description")
                .image("old/path")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(postRepository.save(any(PostEntity.class))).thenReturn(post);

        PostDto expectedDto = new PostDto();
        expectedDto.setDescription("Old description"); // Opis nie powinien się zmienić
        expectedDto.setImage("new/path"); // Obrazek się zmieni

        when(postMapper.mapTo(any(PostEntity.class))).thenReturn(expectedDto);

        PostDto result = postService.updatePost(postId, username, file, null);

        assertNotNull(result);
        assertEquals("Old description", result.getDescription()); // Opis nie zmieniony
        assertEquals("new/path", result.getImage()); // Obrazek zmieniony
    }

    @Test
    void updatePost_shouldNotUpdateDescriptionWhenEmpty() {
        Long postId = 1L;
        String username = "user@example.com";
        UserEntity user = new UserEntity();
        user.setId(1L);

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(fileService.updateFile(file, "old/path", "postImages/")).thenReturn("new/path");

        PostEntity post = PostEntity.builder()
                .id(postId)
                .user(user)
                .description("Old description")
                .image("old/path")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(postRepository.save(any(PostEntity.class))).thenReturn(post);

        PostDto expectedDto = new PostDto();
        expectedDto.setDescription("Old description"); // Opis nie powinien się zmienić
        expectedDto.setImage("new/path"); // Obrazek się zmienia

        when(postMapper.mapTo(any(PostEntity.class))).thenReturn(expectedDto);

        PostDto result = postService.updatePost(postId, username, file, "");

        assertNotNull(result);
        assertEquals("Old description", result.getDescription()); // Opis nie zmieniony
        assertEquals("new/path", result.getImage()); // Obrazek zmieniony
    }


    @Test
    void deletePost_shouldDeletePost() {
        Long postId = 1L;
        String username = "user@example.com";
        UserEntity user = new UserEntity();
        user.setId(1L);

        PostEntity postEntity = PostEntity.builder()
                .id(postId)
                .user(user)
                .image("image/path")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(userService.findUserByEmail(username)).thenReturn(user);

        postService.deletePost(postId, username);

        verify(postRepository, times(1)).deleteById(postId);
        verify(fileService, times(1)).deleteFile("image/path");
    }

    @Test
    void getPostsByUser_shouldReturnUserPosts() {
        String nickName = "user123";
        UserDto user = new UserDto();
        user.setId(1L);
        Pageable pageable = mock(Pageable.class);
        PostEntity postEntity = PostEntity.builder().id(1L).description("Test post").build();
        PostDto postDto = new PostDto();
        postDto.setDescription("Test post");

        when(userService.findUserByNickname(nickName)).thenReturn(user);
        when(postRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(postEntity)));

        when(postMapper.mapTo(any(PostEntity.class))).thenReturn(postDto);

        Page<PostDto> result = postService.getPostsByUser(nickName, pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());
        assertEquals("Test post", result.getContent().get(0).getDescription());
    }

    @Test
    void getPosts_shouldReturnAllPosts() {
        String username = "user@example.com";
        Pageable pageable = mock(Pageable.class);
        UserEntity user = new UserEntity();
        user.setId(1L);

        PostEntity postEntity = PostEntity.builder().id(1L).description("Test post").build();
        PostDto postDto = new PostDto();
        postDto.setDescription("Test post");

        when(userService.findUserByEmail(username)).thenReturn(user);
        when(postRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(postEntity)));
        when(postMapper.mapTo(any(PostEntity.class))).thenReturn(postDto);
        when(userLikeChecker.isPostLikedByUser(any(), anyLong())).thenReturn(true);

        Page<PostDto> result = postService.getPosts(pageable, username);

        assertFalse(result.isEmpty());
        assertTrue(result.getContent().get(0).isLikedByUser());
    }

    @Test
    void deletePost_shouldThrowExceptionWhenPostNotFound() {
        Long postId = 1L;
        String username = "user@example.com";

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                postService.deletePost(postId, username)
        );

        assertEquals(BusinessErrorCodes.POST_NOT_FOUND, exception.getErrorCode());
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void deletePost_shouldThrowExceptionWhenUserNotOwner() {
        Long postId = 1L;
        String username = "user@example.com";
        UserEntity user1 = new UserEntity();
        user1.setId(1L);

        UserEntity user2 = new UserEntity();
        user2.setId(2L); // Inny właściciel posta

        PostEntity post = PostEntity.builder()
                .id(postId)
                .user(user2)
                .image("some/path")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserByEmail(username)).thenReturn(user1);

        CustomException exception = assertThrows(CustomException.class, () ->
                postService.deletePost(postId, username)
        );

        assertEquals(BusinessErrorCodes.POST_MISMATCH, exception.getErrorCode());
        verify(postRepository, times(1)).findById(postId);
        verify(userService, times(1)).findUserByEmail(username);
    }
}
