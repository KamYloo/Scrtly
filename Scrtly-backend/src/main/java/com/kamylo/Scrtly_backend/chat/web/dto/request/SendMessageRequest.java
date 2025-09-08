package com.kamylo.Scrtly_backend.chat.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {
    private Integer chatId;

    @NotBlank(message = "{chat.message.text.notblank}")
    @Size(max = 1000, message = "{chat.message.text.size}")
    private String message;
}
