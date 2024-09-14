package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.StoryException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Story;
import com.kamylo.Scrtly_backend.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface StoryService {
    Story createStory(Story story, Long userId) throws UserException;
    Story findStoryById(Long storyId) throws StoryException;
    List<Story> getStoriesByUserId(Long userId) throws UserException, StoryException;
    Map<User, List<Story>> getGroupedStoriesByFollowedUsers(Long userId);
    void deleteStory(Long storyId, Long userId) throws UserException, StoryException;
}
