package com.kamylo.Scrtly_backend.dto.request;


import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatRoomRequest {
    private List<Long> userIds;
    private String chatRoomName;
}
