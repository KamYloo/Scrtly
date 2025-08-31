package com.kamylo.Scrtly_backend.chat.web.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatRoomRequest {

    @NotEmpty(message = "{chat.participants.notempty}")
    @Size(min = 1, message = "{chat.participants.min}")
    private List<@NotNull(message = "{chat.participant.notnull}") @Positive(message = "{chat.participant.positive}") Long> userIds;

    @Size(max = 100, message = "{chat.name.size}")
    private String chatRoomName;
}
