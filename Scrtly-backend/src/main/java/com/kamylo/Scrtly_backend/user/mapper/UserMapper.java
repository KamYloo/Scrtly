package com.kamylo.Scrtly_backend.user.mapper;

import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface UserMapper {

    @Mapping(target = "observersCount", expression = "java(count(user.getFollowers()))")
    @Mapping(target = "observationsCount", expression = "java(count(user.getFollowings()))")
    @Mapping(target = "observed",  ignore = true)
    @Mapping(target = "isPremium", ignore = true)
    UserDto toDto(UserEntity user);

    @InheritInverseConfiguration
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followings", ignore = true)
    UserEntity toEntity(UserDto dto);

    default int count(java.util.Collection<?> coll) {
        return coll != null ? coll.size() : 0;
    }
}