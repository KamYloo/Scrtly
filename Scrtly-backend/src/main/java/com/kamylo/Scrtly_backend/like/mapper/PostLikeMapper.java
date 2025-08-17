package com.kamylo.Scrtly_backend.like.mapper;

import com.kamylo.Scrtly_backend.like.domain.LikeEntity;
import com.kamylo.Scrtly_backend.like.web.dto.PostStatsDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface PostLikeMapper {

    @Mapping(target = "postId", expression = "java(entity.getPost().getId())")
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "likedByUser", ignore = true)
    PostStatsDto toDto(LikeEntity entity);

    @AfterMapping
    default void enrich(LikeEntity entity, @MappingTarget PostStatsDto dto) {
        var post = entity.getPost();
        dto.setLikeCount(post.getLikes() != null ? post.getLikes().size() : 0);
        dto.setCommentCount(post.getComments() != null ? post.getComments().size() : 0);
    }
}