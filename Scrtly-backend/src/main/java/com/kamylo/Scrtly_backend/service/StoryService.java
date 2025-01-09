package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.StoryDto;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface StoryService {
    StoryDto createStory(String username, MultipartFile storyImage);
    List<StoryDto> getStoriesByUser(String username);
    Map<UserEntity, List<StoryDto>> getGroupedStoriesByFollowedUsers(String username);
    void deleteStory(Long storyId, String username);
}
