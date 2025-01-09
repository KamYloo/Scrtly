package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.PostDto;
import com.kamylo.Scrtly_backend.entity.PostEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.PostRepository;
import com.kamylo.Scrtly_backend.service.PostService;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.utils.UserLikeChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final FileServiceImpl fileService;
    private final Mapper<PostEntity, PostDto> postMapper;
    private final UserLikeChecker userLikeChecker;

    @Override
    @Transactional
    public PostDto createPost(String username, String description, MultipartFile postImage) {
       UserEntity user = userService.findUserByEmail(username);
       String imagePath = null;
       if (!postImage.isEmpty()) {
           imagePath = fileService.saveFile(postImage, "postImages/");
       }
       PostEntity newPost = PostEntity.builder()
               .user(user)
               .description(description)
               .image(imagePath)
               .build();

       PostEntity savedPost = postRepository.save(newPost);
       return postMapper.mapTo(savedPost);
    }

    @Override
    @Transactional
    public PostDto updatePost(Long postId, String username, MultipartFile file, String description) {
       PostEntity post = postRepository.findById(postId).orElseThrow(
               () -> new CustomException(BusinessErrorCodes.POST_NOT_FOUND));

       if (validatePostOwnership(username, post)) {
           if (file != null && !file.isEmpty()) {
               String imagePath = fileService.updateFile(file, post.getImage(), "postImages/");
               post.setImage(imagePath);
           }
           if (description != null && !description.isEmpty()) {
               post.setDescription(description);
           }
           return postMapper.mapTo(postRepository.save(post));
       } else {
           throw new CustomException(BusinessErrorCodes.POST_MISMATCH);
       }
    }

    @Override
    @Transactional
    public void deletePost(Long postId, String username) {
        PostEntity post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.POST_NOT_FOUND));
        if (validatePostOwnership(username, post)) {
            postRepository.deleteById(postId);
            fileService.deleteFile(post.getImage());
        } else {
            throw new CustomException(BusinessErrorCodes.POST_MISMATCH);
        }
    }

    @Override
    public Page<PostDto> getPostsByUser(Long userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable).map(postMapper::mapTo);
    }

    @Override
    public Page<PostDto> getPosts(Pageable pageable, String username) {
        UserEntity user = userService.findUserByEmail(username);
        return postRepository.findAll(pageable).map(postEntity -> {
            PostDto postDto = postMapper.mapTo(postEntity);
            postDto.setLikedByUser(userLikeChecker.isPostLikedByUser(postEntity, user.getId()));
            return postDto;
        });
    }

    private boolean validatePostOwnership(String username, PostEntity post) {
        UserEntity user = userService.findUserByEmail(username);
        return user.getId().equals(post.getUser().getId());
    }
}
