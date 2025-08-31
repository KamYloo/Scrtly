package com.kamylo.Scrtly_backend.chat.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMessageRequest {
    @NotNull(message = "{chat.message.id.notnull}")
    @Positive(message = "{chat.message.id.positive}")
    private Long messageId;
}