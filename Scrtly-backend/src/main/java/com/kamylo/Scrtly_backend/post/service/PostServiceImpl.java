package com.kamylo.Scrtly_backend.post.service;

import com.kamylo.Scrtly_backend.like.repository.LikeRepository;
import com.kamylo.Scrtly_backend.post.mapper.PostMapper;
import com.kamylo.Scrtly_backend.post.web.dto.PostDto;
import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.common.service.impl.FileServiceImpl;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.post.repository.PostRepository;
import com.kamylo.Scrtly_backend.notification.service.NotificationService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.post.repository.PostSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final FileServiceImpl fileService;
    private final PostMapper postMapper;
    private final LikeRepository likeRepository;
    private final NotificationService notificationService;


    @Override
    @Transactional
    public PostDto createPost(String username, String description, MultipartFile postImage) {
       UserEntity user = userService.findUserByEmail(username);
        String imagePath = null;

        if (postImage != null && !postImage.isEmpty()) {
            imagePath = fileService.saveFile(postImage, "postImages/");
        }
       PostEntity newPost = PostEntity.builder()
               .user(user)
               .description(description)
               .image(imagePath)
               .commentCount(0)
               .likeCount(0)
               .build();

       PostEntity savedPost = postRepository.save(newPost);
       return postMapper.toDto(savedPost);
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
           return postMapper.toDto(postRepository.save(post));
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
            if (post.getImage() != null && !post.getImage().isEmpty()) {
                fileService.deleteFile(post.getImage());
            }
            notificationService.deleteNotificationsByPost(post);
            postRepository.deleteById(postId);
        } else {
            throw new CustomException(BusinessErrorCodes.POST_MISMATCH);
        }
    }

    @Override
    public Page<PostDto> getPostsByUser(String nickName, Pageable pageable) {

        UserDto user = userService.findUserByNickname(nickName);
        return postRepository.findByUserId(user.getId(), pageable).map(postMapper::toDto);
    }

    @Override
    public Page<PostDto> getPosts(Pageable pageable, String username, Integer minLikes, Integer maxLikes) {
        Specification<PostEntity> spec = Specification
                .where(PostSpecification.hasMinLikes(minLikes))
                .and(PostSpecification.hasMaxLikes(maxLikes));

        Page<PostEntity> page = postRepository.findAll(spec, pageable);

        if (page.isEmpty()) {
            return page.map(postMapper::toDto);
        }

        Set<Long> likedPostIds = new HashSet<>();
        if (username != null) {
            UserEntity user = userService.findUserByEmail(username);

            List<Long> postIdsOnPage = page.getContent().stream()
                    .map(PostEntity::getId)
                    .toList();

            likedPostIds.addAll(likeRepository.findPostIdsLikedByUser(user.getId(), postIdsOnPage));
        }

        return page.map(postEntity -> {
            PostDto dto = postMapper.toDto(postEntity);
            dto.setLikedByUser(likedPostIds.contains(postEntity.getId()));
            return dto;
        });
    }

    private boolean validatePostOwnership(String username, PostEntity post) {
        UserEntity user = userService.findUserByEmail(username);
        return user.getId().equals(post.getUser().getId());
    }
}
