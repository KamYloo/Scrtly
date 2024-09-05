package com.kamylo.Scrtly_backend.service;


import com.kamylo.Scrtly_backend.exception.PostException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Post;
import com.kamylo.Scrtly_backend.request.SendPostRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {
    Post createPost(SendPostRequest sendPostRequest) throws UserException;
    Post updatePost(Long postId) throws UserException, PostException;
    void deletePost(Long postId, Long userId) throws UserException, PostException;
    List<Post> getAllPostsByUser(Long userId) throws UserException;
    List<Post> getAllPosts() ;
}
