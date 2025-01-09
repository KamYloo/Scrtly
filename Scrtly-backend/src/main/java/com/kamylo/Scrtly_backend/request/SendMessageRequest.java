package com.kamylo.Scrtly_backend.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {
    private String message;
    private Integer chatId;
}
