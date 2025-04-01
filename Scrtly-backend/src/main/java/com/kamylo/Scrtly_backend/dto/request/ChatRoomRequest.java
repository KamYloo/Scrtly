package com.kamylo.Scrtly_backend.dto.request;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatRoomRequest {
    private Long userId;

    public ChatRoomRequest(Long userId) {
        super();
        this.userId = userId;
    }
}
