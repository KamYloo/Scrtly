package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.story.service.StoryService;
import com.kamylo.Scrtly_backend.story.web.controller.StoryController;
import com.kamylo.Scrtly_backend.story.web.dto.StoryDto;
import com.kamylo.Scrtly_backend.story.web.dto.request.StoryRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoryControllerTest {

    @Mock
    private StoryService storyService;

    @InjectMocks
    private StoryController controller;

    @Mock
    private Principal principal;

    private StoryDto sampleStory(Long id, String img) {
        return StoryDto.builder()
                .id(id)
                .image(img)
                .timestamp(LocalDateTime.now())
                .user(null)
                .build();
    }

    @Test
    void createStoryHandler_returnsCreated_andCallsService() {
        MultipartFile file = mock(MultipartFile.class);
        StoryRequest req = StoryRequest.builder().file(file).build();
        when(principal.getName()).thenReturn("author");
        StoryDto dto = sampleStory(1L, "img.png");
        when(storyService.createStory("author", file)).thenReturn(dto);

        var resp = controller.createStoryHandler(req, principal);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(storyService).createStory("author", file);
    }

    @Test
    void getStoriesByUser_returnsOk_andCallsService() {
        when(principal.getName()).thenReturn("viewer");
        List<StoryDto> stories = List.of(sampleStory(2L, "i1"));
        when(storyService.getStoriesByUser("viewer")).thenReturn(stories);

        var resp = controller.getStoriesByUser(principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(stories, resp.getBody());
        verify(storyService).getStoriesByUser("viewer");
    }

    @Test
    void getStoriesByFollowedUsers_returnsOk_andCallsService() {
        when(principal.getName()).thenReturn("viewer");
        Map<String, List<StoryDto>> grouped = Map.of("friend", List.of(sampleStory(3L, "i2")));
        when(storyService.getGroupedStoriesByFollowedUsers("viewer")).thenReturn(grouped);

        var resp = controller.getStoriesByFollowedUsers(principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(grouped, resp.getBody());
        verify(storyService).getGroupedStoriesByFollowedUsers("viewer");
    }

    @Test
    void deleteStory_callsService_andReturnsNoContent() {
        Long id = 5L;
        when(principal.getName()).thenReturn("owner");

        var resp = controller.deleteStory(id, principal);

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(storyService).deleteStory(id, "owner");
    }
}