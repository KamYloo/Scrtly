package com.kamylo.Scrtly_backend.like.mapper;


import com.kamylo.Scrtly_backend.like.domain.SongLikeEntity;
import com.kamylo.Scrtly_backend.like.web.dto.SongLikeDto;
import com.kamylo.Scrtly_backend.user.mapper.UserMapper;
import com.kamylo.Scrtly_backend.song.mapper.SongMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        uses = {UserMapper.class, SongMapper.class}
)
public interface SongLikeMapper {
    SongLikeDto toDto(SongLikeEntity entity);
}
