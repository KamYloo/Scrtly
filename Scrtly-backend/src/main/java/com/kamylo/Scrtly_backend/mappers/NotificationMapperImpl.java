package com.kamylo.Scrtly_backend.mappers;

import com.kamylo.Scrtly_backend.dto.Minimal.PostMinimalDto;
import com.kamylo.Scrtly_backend.dto.Minimal.UserMinimalDto;
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
    public NotificationDto mapTo(NotificationEntity e) {
        UserMinimalDto user = UserMinimalDto.builder()
                .id(e.getRecipient().getId())
                .fullName(e.getRecipient().getFullName())
                .nickName(e.getRecipient().getNickName())
                .profilePicture(e.getRecipient().getProfilePicture())
                .build();

        PostMinimalDto post = PostMinimalDto.builder()
                .id(e.getPost().getId())
                .image(e.getPost().getImage())
                .build();

        return NotificationDto.builder()
                .id(e.getId())
                .message(e.getMessage())
                .seen(e.isSeen())
                .createdDate(e.getCreatedDate())
                .updatedDate(e.getUpdatedDate())
                .count(e.getCount())
                .recipient(user)
                .post(post)
                .build();
    }

    @Override
    public NotificationEntity mapFrom(NotificationDto notificationDto) {
        return modelMapper.map(notificationDto, NotificationEntity.class);
    }
}
