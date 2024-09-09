package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.StoryException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Story;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StoryService {
    Story createStory(Story story, Long userId) throws UserException;
    Story findStoryById(Long storyId) throws StoryException;
    List<Story> getStoriesByUserId(Long userId) throws UserException, StoryException;
    void deleteStory(Long storyId, Long userId) throws UserException, StoryException;
}
