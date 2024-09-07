package com.kamylo.Scrtly_backend.dto.mapper;

import com.kamylo.Scrtly_backend.dto.PostDto;
import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.model.Post;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.util.PostUtil;

import java.util.ArrayList;
import java.util.List;

public class PostDtoMapper {

    public static PostDto toPostDto(Post post, User reqUser) {
        UserDto user = UserDtoMapper.toUserDto(post.getUser());

        boolean isLiked= PostUtil.islikedbyReqUser(reqUser, post);

        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setDescription(post.getDescription());
        postDto.setImage(post.getImage());
        postDto.setCreationDate(post.getCreationDate());
        postDto.setTotalLikes(post.getLikes().size());
        postDto.setTotalComments(post.getComments().size());
        postDto.setUser(user);
        postDto.setLiked(isLiked);

        return postDto;
    }

    public static List<PostDto> toPostDtoList(List<Post> posts, User reqUser) {
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            PostDto postDto = toPostDto(post, reqUser);
            postDtos.add(postDto);
        }
        return postDtos;
    }

}
