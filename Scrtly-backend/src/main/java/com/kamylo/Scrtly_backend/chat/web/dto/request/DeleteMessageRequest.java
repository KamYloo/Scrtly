package com.kamylo.Scrtly_backend.chat.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMessageRequest {
    private Long messageId;
}