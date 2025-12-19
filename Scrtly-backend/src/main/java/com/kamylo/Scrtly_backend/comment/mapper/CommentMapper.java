package com.kamylo.Scrtly_backend.comment.mapper;

import com.kamylo.Scrtly_backend.comment.domain.CommentEntity;
import com.kamylo.Scrtly_backend.comment.web.dto.CommentDto;
import com.kamylo.Scrtly_backend.user.mapper.UserMinimalMapper;
import com.kamylo.Scrtly_backend.post.mapper.PostMinimalMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        uses = {UserMinimalMapper.class, PostMinimalMapper.class}
)
public interface CommentMapper {
    @Mapping(source = "parentComment.id",   target = "parentCommentId")
    @Mapping(target = "likedByUser", ignore = true)
    CommentDto toDto(CommentEntity entity);

    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "replies", ignore = true)
    CommentEntity toEntity(CommentDto dto);
}
