package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.dto.ChatRoomDto;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class RealTimeChat {

    private final SimpMessagingTemplate template;

    @MessageMapping("/message")
    public ChatMessageDto receiveMessage(@Payload ChatMessageDto  chatMessage) {

        ChatRoomDto chatRoom = chatMessage.getChatRoom();

        String user1Destination = "/queue/private/" + chatRoom.getFirstPerson().getId();
        String user2Destination = "/queue/private/" + chatRoom.getSecondPerson().getId();

        template.convertAndSend(user1Destination, chatMessage);
        template.convertAndSend(user2Destination, chatMessage);

        return chatMessage;
    }
}
