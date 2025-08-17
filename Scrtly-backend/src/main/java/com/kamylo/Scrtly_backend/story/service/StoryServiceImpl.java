package com.kamylo.Scrtly_backend.story.service;

import com.kamylo.Scrtly_backend.story.mapper.StoryMapper;
import com.kamylo.Scrtly_backend.story.web.dto.StoryDto;
import com.kamylo.Scrtly_backend.common.service.impl.FileServiceImpl;
import com.kamylo.Scrtly_backend.story.domain.StoryEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.story.repository.StoryRepository;
import com.kamylo.Scrtly_backend.user.service.UserService;
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
    private final StoryMapper storyMapper;

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
        return storyMapper.toDto(savedStory);
    }

    @Override
    public List<StoryDto> getStoriesByUser(String username) {
        UserEntity user = userService.findUserByEmail(username);

        List<StoryEntity> stories = storyRepository.getStoriesByUserId(user.getId());
        return stories.stream().map(storyMapper::toDto).toList();
    }

    @Override
    public Map<String, List<StoryDto>> getGroupedStoriesByFollowedUsers(String username) {
        UserEntity user = userService.findUserByEmail(username);
        List<StoryEntity> stories = storyRepository.getStoriesByFollowedUsers(user.getId());
        List<StoryDto> dtoList = stories.stream()
                .map(storyMapper::toDto)
                .toList();

        return dtoList.stream()
                .collect(Collectors.groupingBy(
                        dto -> dto.getUser().getNickName(),
                        Collectors.toList()
                ));
    }

    @Override
    @Transactional
    public void deleteStory(Long storyId, String username) {
        StoryEntity story = storyRepository.findById(storyId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.STORY_NOT_FOUND));

        if (validateStoryOwnership(username, story)) {
            fileService.deleteFile(story.getImage());
            storyRepository.deleteById(storyId);
        } else {
            throw new CustomException(BusinessErrorCodes.STORY_MISMATCH);
        }
    }

    private boolean validateStoryOwnership(String username, StoryEntity story) {
        UserEntity user = userService.findUserByEmail(username);
        return user.getId().equals(story.getUser().getId());
    }
}
