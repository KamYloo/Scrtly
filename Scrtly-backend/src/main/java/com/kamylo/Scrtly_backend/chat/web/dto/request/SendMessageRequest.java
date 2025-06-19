package com.kamylo.Scrtly_backend.chat.web.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {
    private Integer chatId;
    private String message;

}
