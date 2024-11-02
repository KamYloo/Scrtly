package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.StoryException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Story;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.StoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StoryServiceImplementationTest {

    @Mock
    private StoryRepository storyRepository;

    @Mock
    private UserService userService;

    @Mock
    private FileServiceImplementation fileService;

    @InjectMocks
    private StoryServiceImplementation storyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateStorySuccess() throws UserException {
        Long userId = 1L;
        MultipartFile storyImage = mock(MultipartFile.class);
        User user = new User();
        user.setId(userId);

        when(userService.findUserById(userId)).thenReturn(user);
        when(storyImage.isEmpty()).thenReturn(false);
        when(fileService.saveFile(storyImage, "/uploads/storyImages")).thenReturn("image.jpg");
        when(storyRepository.save(any(Story.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Story result = storyService.createStory(userId, storyImage);

        assertNotNull(result);
        assertEquals("/uploads/storyImages/image.jpg", result.getImage());
        verify(storyRepository, times(1)).save(result);
    }

    @Test
    void testCreateStoryWithoutImage() throws UserException {
        Long userId = 1L;
        MultipartFile storyImage = mock(MultipartFile.class);
        User user = new User();
        user.setId(userId);

        when(userService.findUserById(userId)).thenReturn(user);
        when(storyImage.isEmpty()).thenReturn(true);
        when(storyRepository.save(any(Story.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Story result = storyService.createStory(userId, storyImage);

        assertNotNull(result);
        assertNull(result.getImage());
        verify(storyRepository, times(1)).save(result);
    }

    @Test
    void testFindStoryByIdSuccess() throws StoryException {
        Long storyId = 1L;
        Story story = new Story();
        story.setId(storyId);

        when(storyRepository.findById(storyId)).thenReturn(Optional.of(story));

        Story result = storyService.findStoryById(storyId);

        assertNotNull(result);
        assertEquals(storyId, result.getId());
    }

    @Test
    void testFindStoryByIdNotFound() {
        Long storyId = 1L;

        when(storyRepository.findById(storyId)).thenReturn(Optional.empty());

        assertThrows(StoryException.class, () -> storyService.findStoryById(storyId));
    }

    @Test
    void testGetStoriesByUserIdSuccess() throws UserException, StoryException {
        Long userId = 1L;
        User user = new User();
        Story story = new Story();
        user.setStories(List.of(story));

        when(userService.findUserById(userId)).thenReturn(user);

        List<Story> result = storyService.getStoriesByUserId(userId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetStoriesByUserIdNoStories() throws UserException {
        Long userId = 1L;
        User user = new User();
        user.setStories(Collections.emptyList());

        when(userService.findUserById(userId)).thenReturn(user);

        assertThrows(StoryException.class, () -> storyService.getStoriesByUserId(userId));
    }

    @Test
    void testGetGroupedStoriesByFollowedUsers() {
        Long userId = 1L;
        User followedUser = new User();
        Story story = new Story();
        story.setUser(followedUser);

        when(storyRepository.getStoriesByFollowedUsers(userId)).thenReturn(List.of(story));

        Map<User, List<Story>> result = storyService.getGroupedStoriesByFollowedUsers(userId);

        assertNotNull(result);
        assertTrue(result.containsKey(followedUser));
        assertEquals(1, result.get(followedUser).size());
    }

    @Test
    void testDeleteStorySuccess() throws UserException, StoryException {
        Long storyId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Story story = new Story();
        story.setId(storyId);
        story.setUser(user);
        story.setImage("/uploads/storyImages/image.jpg");

        when(storyRepository.findById(storyId)).thenReturn(Optional.of(story));

        storyService.deleteStory(storyId, userId);

        verify(storyRepository, times(1)).deleteById(storyId);
        verify(fileService, times(1)).deleteFile("/uploads/storyImages/image.jpg");
    }

    @Test
    void testDeleteStoryWithUnauthorizedUser() {
        Long storyId = 1L;
        Long userId = 1L;
        Long otherUserId = 2L;
        User user = new User();
        user.setId(otherUserId);
        Story story = new Story();
        story.setId(storyId);
        story.setUser(user);

        when(storyRepository.findById(storyId)).thenReturn(Optional.of(story));

        assertThrows(UserException.class, () -> storyService.deleteStory(storyId, userId));
        verify(storyRepository, never()).deleteById(storyId);
    }
}
