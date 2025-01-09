package com.kamylo.Scrtly_backend.service;


import com.kamylo.Scrtly_backend.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    PostDto createPost(String username, String description, MultipartFile postImage) ;
    PostDto updatePost(Long postId, String username, MultipartFile file, String description);
    void deletePost(Long postId, String username);
    Page<PostDto> getPostsByUser(Long userId, Pageable pageable);
    Page<PostDto> getPosts(Pageable pageable, String username) ;
}
