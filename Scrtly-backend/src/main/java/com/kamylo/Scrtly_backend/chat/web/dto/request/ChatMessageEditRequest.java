package com.kamylo.Scrtly_backend.chat.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatMessageEditRequest {
    @NotNull(message = "{chat.message.id.notnull}")
    private Long id;

    @NotBlank(message = "{chat.message.text.notblank}")
    @Size(max = 1000, message = "{chat.message.text.size}")
    private String message;
}

