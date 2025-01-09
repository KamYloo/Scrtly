package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.StoryDto;
import com.kamylo.Scrtly_backend.entity.StoryEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.StoryRepository;
import com.kamylo.Scrtly_backend.service.StoryService;
import com.kamylo.Scrtly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final UserService userService;
    private final FileServiceImpl fileService;
    private final Mapper<StoryEntity, StoryDto> storyMapper;

    @Override
    @Transactional
    public StoryDto createStory(String username, MultipartFile storyImage) {
        UserEntity user = userService.findUserByEmail(username);

        String imagePath = null;
        if (!storyImage.isEmpty()) {
            imagePath = fileService.saveFile(storyImage, "storyImages/");
        }

        StoryEntity story = StoryEntity.builder()
                .image(imagePath)
                .user(user)
                .build();

        StoryEntity savedStory = storyRepository.save(story);
        return storyMapper.mapTo(savedStory);
    }

    @Override
    public List<StoryDto> getStoriesByUser(String username) {
        UserEntity user = userService.findUserByEmail(username);

        List<StoryEntity> stories = storyRepository.getStoriesByUserId(user.getId());
        return stories.stream().map(storyMapper::mapTo).toList();
    }

    @Override
    public Map<UserEntity, List<StoryDto>> getGroupedStoriesByFollowedUsers(String username) {
        UserEntity user = userService.findUserByEmail(username);
        List<StoryEntity> stories = storyRepository.getStoriesByFollowedUsers(user.getId());
        return stories.stream()
                .collect(Collectors.groupingBy(
                        StoryEntity::getUser,
                        Collectors.mapping(storyMapper::mapTo, Collectors.toList())
                ));
    }

    @Override
    @Transactional
    public void deleteStory(Long storyId, String username) {
        StoryEntity story = storyRepository.findById(storyId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.STORY_NOT_FOUND));

        if (validateStoryOwnership(username, story)) {
            storyRepository.deleteById(storyId);
            fileService.deleteFile(story.getImage());
        } else {
            throw new CustomException(BusinessErrorCodes.STORY_MISMATCH);
        }
    }

    private boolean validateStoryOwnership(String username, StoryEntity story) {
        UserEntity user = userService.findUserByEmail(username);
        return user.getId().equals(story.getUser().getId());
    }
}
