package com.kamylo.Scrtly_backend.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationRabbitConfig {

    @Value("${notification.exchange}")
    private String notifExchangeName;

    @Value("${notification.queue}")
    private String notifQueueName;

    @Value("${notification.routing-key-pattern}")
    private String notifRoutingKey;

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(notifExchangeName, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(notifQueueName, true);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(notificationExchange)
                .with(notifRoutingKey);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(converter);
        template.setExchange(notifExchangeName);
        template.setRoutingKey(notifRoutingKey);
        template.setMandatory(true);
        return template;
    }
}
