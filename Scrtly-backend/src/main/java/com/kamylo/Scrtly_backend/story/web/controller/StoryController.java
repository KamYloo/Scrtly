package com.kamylo.Scrtly_backend.story.web.controller;

import com.kamylo.Scrtly_backend.story.web.dto.StoryDto;
import com.kamylo.Scrtly_backend.story.service.StoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/stories")
public class StoryController {

    private final StoryService storyService;

    @PostMapping("/create")
    public ResponseEntity<StoryDto> createStoryHandler(@RequestParam("file") MultipartFile file, Principal principal) {
        StoryDto story = storyService.createStory(principal.getName(), file);
        return new ResponseEntity<>(story, HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<List<StoryDto>> getStoriesByUser(Principal principal) {
        List<StoryDto> stories = storyService.getStoriesByUser(principal.getName());
        return new ResponseEntity<>(stories, HttpStatus.OK);
    }

    @GetMapping("/followed")
    public ResponseEntity<Map<String, List<StoryDto>>> getStoriesByFollowedUsers(Principal principal) {
        Map<String, List<StoryDto>> groupedStories = storyService.getGroupedStoriesByFollowedUsers(principal.getName());
        return new ResponseEntity<>(groupedStories, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{storyId}")
    public ResponseEntity<?> deleteStory(@PathVariable Long storyId, Principal principal) {
       storyService.deleteStory(storyId, principal.getName());
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
