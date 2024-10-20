package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.StoryException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Story;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private FileServiceImplementation fileService;

    @Override
    public Story createStory(Long userId, MultipartFile storyImage) throws UserException {
        User user = userService.findUserById(userId);
        Story story = new Story();
        story.setUser(user);

        if (!storyImage.isEmpty()) {
            String imagePath = fileService.saveFile(storyImage, "/uploads/storyImages");
            story.setImage("/uploads/storyImages/" + imagePath);
        }

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
        fileService.deleteFile(story.getImage());
    }
}
