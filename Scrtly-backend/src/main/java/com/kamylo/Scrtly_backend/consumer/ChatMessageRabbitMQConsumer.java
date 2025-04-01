package com.kamylo.Scrtly_backend.consumer;

import com.kamylo.Scrtly_backend.config.RabbitMQConfig;
import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageRabbitMQConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
    public void receiveMessage(ChatMessageDto chatMessageDto) {
        Integer chatId = chatMessageDto.getChatRoomId();
        messagingTemplate.convertAndSend("/topic/room/" + chatId, chatMessageDto);
    }
}