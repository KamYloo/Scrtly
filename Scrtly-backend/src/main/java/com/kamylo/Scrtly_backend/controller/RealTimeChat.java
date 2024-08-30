package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.model.ChatMessage;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RealTimeChat {
    private SimpMessagingTemplate template;

    @Autowired
    public RealTimeChat(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/message")
    public ChatMessage receiveMessage(@Payload ChatMessage message) {

        template.convertAndSend("/group/"+message.getChat().getId().toString() , message);

        return message;
    }
}
