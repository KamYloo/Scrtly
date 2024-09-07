package com.kamylo.Scrtly_backend.dto.mapper;

import com.kamylo.Scrtly_backend.dto.CommentDto;
import com.kamylo.Scrtly_backend.dto.LikeDto;
import com.kamylo.Scrtly_backend.dto.PostDto;
import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.model.Like;
import com.kamylo.Scrtly_backend.model.User;

import java.util.ArrayList;
import java.util.List;

public class LikeDtoMapper {
    public static LikeDto toLikePostDto(Like like, User reqUser) {
        UserDto user = UserDtoMapper.toUserDto(like.getUser());
        UserDto reqUserDto = UserDtoMapper.toUserDto(reqUser);
        PostDto post = PostDtoMapper.toPostDto(like.getPost(), reqUser);

        LikeDto likeDto = new LikeDto();
        likeDto.setUser(user);
        likeDto.setPost(post);
        likeDto.setId(like.getId());

        return likeDto;
    }

    public static LikeDto toLikeCommentDto(Like like, User reqUser) {
        UserDto user = UserDtoMapper.toUserDto(like.getUser());
        UserDto reqUserDto = UserDtoMapper.toUserDto(reqUser);
        CommentDto comment = CommentDtoMapper.commentDto(like.getComment(), reqUser);

        LikeDto likeDto = new LikeDto();
        likeDto.setUser(user);
        likeDto.setComment(comment);
        likeDto.setId(like.getId());

        return likeDto;
    }

    public static List<LikeDto> toLikePostList(List<Like> likes, User reqUser) {
        List<LikeDto> likeDtos = new ArrayList<>();

        for (Like like : likes) {
            UserDto user = UserDtoMapper.toUserDto(like.getUser());
            PostDto post = PostDtoMapper.toPostDto(like.getPost(), reqUser);

            LikeDto likeDto = new LikeDto();
            likeDto.setUser(user);
            likeDto.setPost(post);
            likeDto.setId(like.getId());
            likeDtos.add(likeDto);
        }
        return likeDtos;
    }

    public static List<LikeDto> toLikeCommentList(List<Like> likes, User reqUser) {
        List<LikeDto> likeDtos = new ArrayList<>();

        for (Like like : likes) {
            UserDto user = UserDtoMapper.toUserDto(like.getUser());
            CommentDto comment = CommentDtoMapper.commentDto(like.getComment(), reqUser);

            LikeDto likeDto = new LikeDto();
            likeDto.setUser(user);
            likeDto.setComment(comment);
            likeDto.setId(like.getId());
            likeDtos.add(likeDto);
        }
        return likeDtos;
    }
}
