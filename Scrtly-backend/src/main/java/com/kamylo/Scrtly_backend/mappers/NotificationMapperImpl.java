package com.kamylo.Scrtly_backend.mappers;

import com.kamylo.Scrtly_backend.dto.NotificationDto;
import com.kamylo.Scrtly_backend.entity.NotificationEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotificationMapperImpl implements Mapper<NotificationEntity, NotificationDto> {
    private ModelMapper modelMapper;

    @Override
    public NotificationDto mapTo(NotificationEntity notificationEntity) {
        return modelMapper.map(notificationEntity, NotificationDto.class);
    }

    @Override
    public NotificationEntity mapFrom(NotificationDto notificationDto) {
        return modelMapper.map(notificationDto, NotificationEntity.class);
    }
}
