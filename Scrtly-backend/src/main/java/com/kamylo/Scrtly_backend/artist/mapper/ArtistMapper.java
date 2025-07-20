package com.kamylo.Scrtly_backend.artist.mapper;

import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Optional;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface ArtistMapper {

    @Mapping(source = "user.profilePicture", target = "profilePicture")
    @Mapping(target = "totalFans", expression = "java(countFollowers(entity.getUser()))")
    @Mapping(target = "observed", ignore = true)
    ArtistDto toDto(ArtistEntity entity);

    @InheritInverseConfiguration
    @Mapping(target = "user", ignore = true)
    ArtistEntity toEntity(ArtistDto dto);

    default int countFollowers(UserEntity user) {
        return Optional.ofNullable(user)
                .map(UserEntity::getFollowers)
                .map(Set::size)
                .orElse(0);
    }
}
