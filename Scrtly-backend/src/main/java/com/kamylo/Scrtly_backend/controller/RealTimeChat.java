package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.model.ChatMessage;
import com.kamylo.Scrtly_backend.model.ChatRoom;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RealTimeChat {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/message")
    public ChatMessage receiveMessage(@Payload ChatMessage message) {

        ChatRoom chatRoom = message.getChatRoom();

        String user1Destination = "/queue/private/" + chatRoom.getFirstPerson().getId();
        String user2Destination = "/queue/private/" + chatRoom.getSecondPerson().getId();

        template.convertAndSend(user1Destination, message);
        template.convertAndSend(user2Destination, message);

        return message;
    }
}
