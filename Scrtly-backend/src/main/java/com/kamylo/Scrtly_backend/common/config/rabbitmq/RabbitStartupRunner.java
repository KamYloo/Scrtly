package com.kamylo.Scrtly_backend.common.config.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitStartupRunner implements ApplicationRunner {
    private final AmqpAdmin amqpAdmin;
    private final TopicExchange chatExchange;
    private final TopicExchange notificationExchange;
    private final Queue chatQueue;
    private final Queue notificationQueue;
    private final Binding chatBinding;
    private final Binding notificationBinding;

    @Override
    public void run(ApplicationArguments args) {
        amqpAdmin.declareExchange(chatExchange);
        amqpAdmin.declareExchange(notificationExchange);
        amqpAdmin.declareQueue(chatQueue);
        amqpAdmin.declareQueue(notificationQueue);
        amqpAdmin.declareBinding(chatBinding);
        amqpAdmin.declareBinding(notificationBinding);
    }
}