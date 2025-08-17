package com.kamylo.Scrtly_backend.common.config.rabbitmq;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MetricsRabbitConfig {
    @Value("${metrics.exchange}")
    private String exchange;

    @Value("${metrics.queue}")
    private String queue;

    @Value("${metrics.routing.view}")
    private String routingView;

    @Value("${metrics.routing.play}")
    private String routingPlay;

    @Value("${metrics.routing.album}")
    private String routingAlbum;

    @Bean
    public TopicExchange metricsExchange() {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public Queue metricsQueue() {
        return new Queue(queue, true);
    }

    @Bean
    public Binding bindArtistView() {
        return BindingBuilder.bind(metricsQueue())
                .to(metricsExchange())
                .with(routingView);
    }

    @Bean
    public Binding bindSongPlay() {
        return BindingBuilder.bind(metricsQueue())
                .to(metricsExchange())
                .with(routingPlay);
    }

    @Bean
    public Binding bindAlbumView() {
        return BindingBuilder.bind(metricsQueue())
                .to(metricsExchange())
                .with(routingAlbum);
    }

    @Bean("metricsRabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf,
                                         Jackson2JsonMessageConverter conv) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(conv);
        t.setExchange(exchange);
        return t;
    }
}
