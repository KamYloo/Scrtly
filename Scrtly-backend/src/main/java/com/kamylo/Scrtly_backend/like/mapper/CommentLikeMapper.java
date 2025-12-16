package com.kamylo.Scrtly_backend.like.mapper;

import com.kamylo.Scrtly_backend.like.domain.LikeEntity;
import com.kamylo.Scrtly_backend.like.web.dto.CommentStatsDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface CommentLikeMapper {
    @Mapping(target = "commentId", expression = "java(entity.getComment().getId())")
    @Mapping(target = "likedByUser", ignore = true)
    CommentStatsDto toDto(LikeEntity entity);
}
