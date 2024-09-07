package com.kamylo.Scrtly_backend.dto.mapper;

import com.kamylo.Scrtly_backend.dto.CommentDto;
import com.kamylo.Scrtly_backend.dto.PostDto;
import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.model.Comment;
import com.kamylo.Scrtly_backend.model.Like;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.util.CommentUtil;

import java.util.ArrayList;
import java.util.List;

public class CommentDtoMapper {

    public static CommentDto commentDto(Comment comment, User reqUser) {
        UserDto user = UserDtoMapper.toUserDto(comment.getUser());
        PostDto post = PostDtoMapper.toPostDto(comment.getPost(), reqUser);
        boolean isLiked = CommentUtil.islikedbyReqUser(reqUser, comment);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setComment(comment.getComment());
        commentDto.setLiked(isLiked);
        commentDto.setCreationDate(comment.getCreationDate());
        commentDto.setTotalLikes(comment.getLikes().size());
        commentDto.setUser(user);
        commentDto.setPost(post);

        return commentDto;
    }

    public static List<CommentDto> commentDtoList(List<Comment> comments, User reqUser) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = commentDto(comment, reqUser);
            commentDtos.add(commentDto);
        }
        return commentDtos;
    }
}
