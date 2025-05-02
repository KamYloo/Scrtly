package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.Minimal.UserMinimalDto;
import com.kamylo.Scrtly_backend.dto.StoryDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface StoryService {
    StoryDto createStory(String username, MultipartFile storyImage);
    List<StoryDto> getStoriesByUser(String username);
    Map<String, List<StoryDto>> getGroupedStoriesByFollowedUsers(String username);
    void deleteStory(Long storyId, String username);
}
