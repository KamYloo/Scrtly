package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.exception.StoryException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Story;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.response.ApiResponse;
import com.kamylo.Scrtly_backend.service.StoryService;
import com.kamylo.Scrtly_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stories")
public class StoryController {
    @Autowired
    private StoryService storyService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Story> createStoryHandler(@RequestBody Story story, @RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findUserProfileByJwt(token);
        Story createdStory = storyService.createStory(story,user.getId());
        return new ResponseEntity<>(createdStory, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Story>> getAllStoryByUserHandler(@RequestHeader("Authorization") String token) throws UserException, StoryException {
        User user = userService.findUserProfileByJwt(token);
        List<Story> stories = storyService.getStoriesByUserId(user.getId());
        return new ResponseEntity<>(stories, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{storyId}")
    public ResponseEntity<ApiResponse> deleteStoryHandler(@PathVariable Long storyId, @RequestHeader("Authorization") String token) throws UserException, StoryException {
        User user = userService.findUserProfileByJwt(token);
        ApiResponse response = new ApiResponse();

        try {
            Story story = storyService.findStoryById(storyId);
            if (story == null) {
                throw new StoryException("Story not found with id " + storyId);
            }
            storyService.deleteStory(storyId, user.getId());
            response.setMessage("Story deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (StoryException | UserException e) {
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
