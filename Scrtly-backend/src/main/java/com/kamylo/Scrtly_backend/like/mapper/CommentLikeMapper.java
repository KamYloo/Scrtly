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
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "likedByUser", ignore = true)
    CommentStatsDto toDto(LikeEntity entity);

    @AfterMapping
    default void enrich(LikeEntity entity, @MappingTarget CommentStatsDto dto) {
        var comment = entity.getComment();
        dto.setLikeCount(comment.getLikes() != null ? comment.getLikes().size() : 0);
    }
}
