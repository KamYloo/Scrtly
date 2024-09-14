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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/stories")
public class StoryController {
    @Autowired
    private StoryService storyService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Story> createStoryHandler(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) throws UserException {

        if (file.isEmpty()) {
            throw new RuntimeException("Image file not uploaded.");
        }

        User user = userService.findUserProfileByJwt(token);
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get("src/main/resources/static/uploads/storyImages").resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Story story = new Story();
            story.setImage("/uploads/storyImages/" + fileName);
            Story createdStory = storyService.createStory(story,user.getId());
            return new ResponseEntity<>(createdStory, HttpStatus.CREATED);

        }catch (IOException e) {
            throw new RuntimeException("Error saving image file.", e);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Story>> getAllStoryByUserHandler(@RequestHeader("Authorization") String token) throws UserException, StoryException {
        User user = userService.findUserProfileByJwt(token);
        List<Story> stories = storyService.getStoriesByUserId(user.getId());
        return new ResponseEntity<>(stories, HttpStatus.OK);
    }

    @GetMapping("/followed")
    public ResponseEntity<Map<User, List<Story>>> getStoriesByFollowedUsersHandler(@RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findUserProfileByJwt(token);
        Map<User, List<Story>> groupedStories = storyService.getGroupedStoriesByFollowedUsers(user.getId());
        return new ResponseEntity<>(groupedStories, HttpStatus.OK);
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
            String imagePath = "src/main/resources/static" + story.getImage();
            Path filePath = Paths.get(imagePath);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            response.setMessage("Story deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (StoryException | UserException e) {
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            response.setMessage("Error deleting the image file.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
