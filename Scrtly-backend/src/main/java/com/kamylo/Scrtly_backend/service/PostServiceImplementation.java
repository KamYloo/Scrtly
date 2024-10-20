package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.PostException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Post;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.PostRepository;
import com.kamylo.Scrtly_backend.request.SendPostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImplementation implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileServiceImplementation fileService;

    @Override
    public Post createPost(SendPostRequest sendPostRequest, MultipartFile postImage) throws UserException {
        User user = userService.findUserById(sendPostRequest.getUser().getId());
        Post newPost = new Post();
        if (!postImage.isEmpty()) {
            String imagePath = fileService.saveFile(postImage, "/uploads/postImages");
            newPost.setImage("/uploads/postImages/" + imagePath);
        }
        newPost.setDescription(sendPostRequest.getDescription());
        newPost.setUser(user);
        newPost.setCreationDate(LocalDateTime.now());

        return postRepository.save(newPost);
    }

    @Override
    public Post updatePost(Long postId) throws UserException, PostException {
        return null;
    }

    @Override
    public void deletePost(Long postId, Long userId) throws UserException, PostException {
        Optional<Post> post = postRepository.findById(postId);

        if (!userId.equals(post.get().getUser().getId())) {
            throw new UserException("You can't delete another user's post");
        }
        postRepository.deleteById(postId);
        fileService.deleteFile(post.get().getImage());
    }

    @Override
    public List<Post> getAllPostsByUser(Long userId) throws UserException {
      return postRepository.findPostByUserId(userId);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreationDateDesc();
    }
}
