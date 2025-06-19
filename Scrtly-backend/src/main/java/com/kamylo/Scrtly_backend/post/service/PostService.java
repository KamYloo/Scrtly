package com.kamylo.Scrtly_backend.post.service;


import com.kamylo.Scrtly_backend.post.web.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    PostDto createPost(String username, String description, MultipartFile postImage) ;
    PostDto updatePost(Long postId, String username, MultipartFile file, String description);
    void deletePost(Long postId, String username);
    Page<PostDto> getPostsByUser(String nickName, Pageable pageable);
    Page<PostDto> getPosts(Pageable pageable, String username, Integer minLikes, Integer maxLikes);
}
