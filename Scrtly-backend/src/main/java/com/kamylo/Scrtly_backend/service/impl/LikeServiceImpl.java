package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.LikeDto;
import com.kamylo.Scrtly_backend.entity.CommentEntity;
import com.kamylo.Scrtly_backend.entity.PostEntity;
import com.kamylo.Scrtly_backend.entity.LikeEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.CommentRepository;
import com.kamylo.Scrtly_backend.repository.LikeRepository;
import com.kamylo.Scrtly_backend.repository.PostRepository;
import com.kamylo.Scrtly_backend.service.LikeService;
import com.kamylo.Scrtly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final UserService userService;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final Mapper<LikeEntity, LikeDto> likeMapper;

   /* @Override
    public List<LikeEntity> getLikesByPost(Long postId) throws PostException {
       Optional<PostEntity> post = postRepository.findById(postId);
       if (post.isPresent()) {
           return likeRepository.findByPostId(post.get().getId());
       }
       throw new PostException("PostEntity not found with id: " + postId);
    }

    @Override
    public List<LikeEntity> getLikesByComment(Long commentId) throws CommentException {
       CommentEntity commentEntity = commentService.findCommentById(commentId);
       if (commentEntity != null) {
           return likeRepository.findByCommentId(commentId);
       }
      throw new CommentException("CommentEntity not found with id: " + commentId);
    }*/

    @Override
    public LikeDto likePost(Long postId, String username) {
        UserEntity user = userService.findUserByEmail(username);
        LikeEntity checkLikeExistPost = likeRepository.isLikeExistPost(user.getId(), postId);

        if (checkLikeExistPost != null) {
            likeRepository.deleteById(checkLikeExistPost.getId());
            return likeMapper.mapTo(checkLikeExistPost);
        }

        PostEntity post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.POST_NOT_FOUND));

        LikeEntity like = LikeEntity.builder()
                .user(user)
                .post(post)
                .build();

        LikeEntity savedLikeEntity = likeRepository.save(like);
        post.getLikes().add(savedLikeEntity);
        postRepository.save(post);
        return likeMapper.mapTo(like);
    }

    @Override
    public LikeDto likeComment(Long commentId, String username) {
        UserEntity user = userService.findUserByEmail(username);
        LikeEntity checkLikeExistComment = likeRepository.isLikeExistComment(user.getId(), commentId);

        if (checkLikeExistComment != null) {
            likeRepository.deleteById(checkLikeExistComment.getId());
            return likeMapper.mapTo(checkLikeExistComment);
        }

        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.COMMENT_NOT_FOUND));

        LikeEntity like = LikeEntity.builder()
                .user(user)
                .comment(comment)
                .build();

        LikeEntity savedLikeEntity = likeRepository.save(like);
        comment.getLikes().add(savedLikeEntity);
        commentRepository.save(comment);
        return likeMapper.mapTo(like);
    }
}
