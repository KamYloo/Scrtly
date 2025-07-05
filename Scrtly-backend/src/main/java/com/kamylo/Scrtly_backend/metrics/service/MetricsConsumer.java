package com.kamylo.Scrtly_backend.metrics.service;

import com.kamylo.Scrtly_backend.metrics.domain.MetricEvent;
import com.kamylo.Scrtly_backend.metrics.domain.enums.MetricType;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MetricsConsumer {
    private final StringRedisTemplate redis;

    @RabbitListener(queues = "${metrics.queue}")
    public void handle(MetricEvent ev) {
        MetricType type = MetricType.fromEvent(ev.getType());
        String prefix  = type.redisPrefix();

        var date   = ev.getTimestamp().atZone(ZoneId.of("Europe/Warsaw")).toLocalDate();
        String day   = date.toString();
        String month = day.substring(0, 7);

        List<String> keys = List.of(
                prefix + ":" + day,
                prefix + ":" + month,
                prefix + ":all"
        );
        String id = ev.getEntityId().toString();
        keys.forEach(k -> redis.opsForZSet().incrementScore(k, id, 1));
    }
}
