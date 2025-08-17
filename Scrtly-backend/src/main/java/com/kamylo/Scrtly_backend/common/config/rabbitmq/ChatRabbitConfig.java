package com.kamylo.Scrtly_backend.common.config.rabbitmq;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatRabbitConfig {

    @Value("${chat.exchange}")
    private String chatExchangeName;

    @Value("${chat.queue}")
    private String chatQueueName;

    @Value("${chat.binding-key-pattern}")
    private String chatRoutingKey;

    private static final Logger log = LoggerFactory.getLogger(ChatRabbitConfig.class);

    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(chatExchangeName,  true, false);
    }

    @Bean
    public Queue chatQueue() {
        return new Queue(chatQueueName, true);
    }

    @Bean
    public Binding chatBinding(Queue chatQueue, TopicExchange chatExchange) {
        return BindingBuilder
                .bind(chatQueue)
                .to(chatExchange)
                .with(chatRoutingKey);
    }

    @Bean("chatRabbitTemplate")
    public RabbitTemplate chatRabbitTemplate(ConnectionFactory cf,
                                             Jackson2JsonMessageConverter conv) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(conv);
        template.setExchange(chatExchangeName);
        template.setRoutingKey(chatRoutingKey);
        return template;
    }

   /* @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(jsonMessageConverter());

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("Wysłanie wiadomości do RabbitMQ nie powiodło się: {}", cause);
                // tutaj np. retry, alert itp.
            }
        });

        template.setReturnsCallback(returned -> {
            log.error("Wiadomość zwrócona: routingKey={}, reason={}",
                    returned.getRoutingKey(),
                    returned.getReplyText());
        });

        return template;
    }*/
}