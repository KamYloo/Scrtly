package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.StoryException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Story;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.StoryRepository;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StoryServiceImplementation implements StoryService {
    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Story createStory(Story story, Long userId) throws UserException {
        User user = userService.findUserById(userId);
        story.setUser(user);
        story.setTimestamp(LocalDateTime.now());
        user.getStories().add(story);
        return storyRepository.save(story);
    }

    @Override
    public Story findStoryById(Long storyId) throws StoryException {
        return storyRepository.findById(storyId).orElseThrow(() -> new StoryException("Story not found with id: " + storyId));
    }

    @Override
    public List<Story> getStoriesByUserId(Long userId) throws UserException, StoryException {
        User user = userService.findUserById(userId);
        List<Story> stories = user.getStories();
        if (stories.isEmpty()) {
            throw new StoryException("user doesn't have any stories");
        }
        return stories;
    }

    @Override
    public Map<User, List<Story>> getGroupedStoriesByFollowedUsers(Long userId) {
        List<Story> stories = storyRepository.getStoriesByFollowedUsers(userId);
        return stories.stream()
                .collect(Collectors.groupingBy(Story::getUser));
    }

    @Override
    public void deleteStory(Long storyId, Long userId) throws UserException, StoryException {
        Story story = findStoryById(storyId);
        if (!userId.equals(story.getUser().getId())) {
            throw new UserException("user doesn't belong to this story");
        }
        storyRepository.deleteById(storyId);
    }
}
