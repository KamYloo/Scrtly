package com.kamylo.Scrtly_backend.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {
    private Integer chatId;
    private String message;

}
