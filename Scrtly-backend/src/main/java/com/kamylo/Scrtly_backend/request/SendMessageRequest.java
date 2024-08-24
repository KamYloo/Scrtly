package com.kamylo.Scrtly_backend.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {
    private Long userId;
    private String message;
    private Integer chatId;
}
