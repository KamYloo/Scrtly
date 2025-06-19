package com.kamylo.Scrtly_backend.chat.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatMessageEditRequest {
    private Long id;
    private String message;
}

