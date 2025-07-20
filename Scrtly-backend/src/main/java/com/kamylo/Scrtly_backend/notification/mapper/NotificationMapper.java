package com.kamylo.Scrtly_backend.notification.mapper;

import com.kamylo.Scrtly_backend.notification.domain.NotificationEntity;
import com.kamylo.Scrtly_backend.notification.web.dto.NotificationDto;
import com.kamylo.Scrtly_backend.post.mapper.PostMinimalMapper;
import com.kamylo.Scrtly_backend.user.mapper.UserMinimalMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        uses = {
                UserMinimalMapper.class,
                PostMinimalMapper.class
        }
)
public interface NotificationMapper {
    NotificationDto toDto(NotificationEntity e);
}
