package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.dto.StoryDto;
import com.kamylo.Scrtly_backend.entity.StoryEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.StoryRepository;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.service.impl.FileServiceImpl;
import com.kamylo.Scrtly_backend.service.impl.StoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class StoryServiceImplTest {

    @Mock
    private StoryRepository storyRepository;

    @Mock
    private UserService userService;

    @Mock
    private FileServiceImpl fileService;

    @Mock
    private Mapper<StoryEntity, StoryDto> storyMapper;

    @InjectMocks
    private StoryServiceImpl storyService;

    private UserEntity user;
    private StoryEntity story;
    private StoryDto storyDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");

        story = StoryEntity.builder()
                .image("old/path")
                .user(user)
                .build();

        storyDto = new StoryDto();
        storyDto.setImage("old/path");
    }


    @Test
    void createStory_shouldCreateStoryWithImage() {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(fileService.saveFile(multipartFile, "storyImages/")).thenReturn("new/image/path");

        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        StoryEntity storyWithImage = StoryEntity.builder()
                .image("new/image/path")
                .user(user)
                .build();
        when(storyRepository.save(any(StoryEntity.class))).thenReturn(storyWithImage);
        StoryDto dtoWithImage = new StoryDto();
        dtoWithImage.setImage("new/image/path");
        when(storyMapper.mapTo(any(StoryEntity.class))).thenReturn(dtoWithImage);

        StoryDto result = storyService.createStory("user@example.com", multipartFile);

        assertNotNull(result);
        assertEquals("new/image/path", result.getImage());
        verify(fileService, times(1)).saveFile(multipartFile, "storyImages/");
        verify(storyRepository, times(1)).save(any(StoryEntity.class));
        verify(storyMapper, times(1)).mapTo(any(StoryEntity.class));
    }

    @Test
    void createStory_shouldCreateStoryWithoutImageWhenFileIsEmpty() {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(true);

        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        StoryEntity storyWithoutImage = StoryEntity.builder()
                .image(null)
                .user(user)
                .build();
        when(storyRepository.save(any(StoryEntity.class))).thenReturn(storyWithoutImage);
        StoryDto dtoWithoutImage = new StoryDto();
        dtoWithoutImage.setImage(null);
        when(storyMapper.mapTo(any(StoryEntity.class))).thenReturn(dtoWithoutImage);

        StoryDto result = storyService.createStory("user@example.com", multipartFile);

        assertNotNull(result);
        assertNull(result.getImage());
        verify(fileService, never()).saveFile(any(MultipartFile.class), anyString());
    }


    @Test
    void getStoriesByUser_shouldReturnStories() {
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        when(storyRepository.getStoriesByUserId(user.getId()))
                .thenReturn(Collections.singletonList(story));
        when(storyMapper.mapTo(story)).thenReturn(storyDto);

        List<StoryDto> result = storyService.getStoriesByUser("user@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(storyDto, result.get(0));
    }


    @Test
    void getGroupedStoriesByFollowedUsers_shouldReturnGroupedStories() {
        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        StoryEntity story1 = StoryEntity.builder().image("path1").user(user).build();
        StoryEntity story2 = StoryEntity.builder().image("path2").user(user2).build();

        StoryDto dto1 = new StoryDto();
        dto1.setImage("path1");
        StoryDto dto2 = new StoryDto();
        dto2.setImage("path2");

        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        when(storyRepository.getStoriesByFollowedUsers(user.getId()))
                .thenReturn(Arrays.asList(story1, story2));
        when(storyMapper.mapTo(story1)).thenReturn(dto1);
        when(storyMapper.mapTo(story2)).thenReturn(dto2);

        Map<UserEntity, List<StoryDto>> result = storyService.getGroupedStoriesByFollowedUsers("user@example.com");

        assertNotNull(result);
        assertTrue(result.containsKey(user));
        assertTrue(result.containsKey(user2));
        assertEquals(1, result.get(user).size());
        assertEquals(1, result.get(user2).size());
        assertEquals(dto1, result.get(user).get(0));
        assertEquals(dto2, result.get(user2).get(0));
    }


    @Test
    void deleteStory_shouldDeleteStoryIfOwner() {
        Long storyId = 1L;
        when(storyRepository.findById(storyId)).thenReturn(Optional.of(story));
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);

        doNothing().when(fileService).deleteFile(story.getImage());

        storyService.deleteStory(storyId, "user@example.com");

        verify(storyRepository, times(1)).deleteById(storyId);
        verify(fileService, times(1)).deleteFile(story.getImage());
    }

    @Test
    void deleteStory_shouldThrowExceptionWhenStoryNotFound() {
        Long storyId = 1L;
        when(storyRepository.findById(storyId)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () ->
                storyService.deleteStory(storyId, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.STORY_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void deleteStory_shouldThrowExceptionWhenNotOwner() {
        Long storyId = 1L;
        UserEntity anotherUser = new UserEntity();
        anotherUser.setId(2L);

        StoryEntity storyNotOwned = StoryEntity.builder()
                .image("some/path")
                .user(anotherUser)
                .build();

        when(storyRepository.findById(storyId)).thenReturn(Optional.of(storyNotOwned));
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);

        CustomException ex = assertThrows(CustomException.class, () ->
                storyService.deleteStory(storyId, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.STORY_MISMATCH, ex.getErrorCode());
        verify(storyRepository, never()).deleteById(anyLong());
        verify(fileService, never()).deleteFile(anyString());
    }
}
