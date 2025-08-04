package com.kamylo.Scrtly_backend.artist.mapper;

import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public abstract class ArtistMapper {

    protected StringRedisTemplate redis;

    @Autowired
    public void setRedis(StringRedisTemplate redis) {
        this.redis = redis;
    }


    @Mapping(source = "user.profilePicture", target = "profilePicture")
    @Mapping(target = "totalFans", expression = "java(countFollowers(entity.getUser()))")
    @Mapping(target = "observed", ignore = true)
    @Mapping(target = "monthlyPlays", ignore = true)
    public abstract ArtistDto toDto(ArtistEntity entity);

    @AfterMapping
    protected void enrichMonthlyPlays(ArtistEntity entity, @MappingTarget ArtistDto dto) {
        String month = LocalDate.now().toString().substring(0,7);
        String key   = "artist:plays:" + entity.getId() + ":" + month;
        Double score = redis.opsForZSet().score(key, entity.getId().toString());
        dto.setMonthlyPlays(score != null ? score.intValue() : 0);
    }

    @InheritInverseConfiguration
    @Mapping(target = "user", ignore = true)
    public abstract ArtistEntity toEntity(ArtistDto dto);


    int countFollowers(UserEntity user) {
        return Optional.ofNullable(user)
                .map(UserEntity::getFollowers)
                .map(Set::size)
                .orElse(0);
    }

}
