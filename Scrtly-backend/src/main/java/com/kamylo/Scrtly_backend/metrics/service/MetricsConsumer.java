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
    public void handle(MetricEvent event) {
        MetricType type = MetricType.fromEvent(event.getType());
        String entityId = event.getEntityId().toString();
        String artistId = event.getArtistId() != null ? event.getArtistId().toString() : null;

        var date = event.getTimestamp()
                .atZone(ZoneId.of("Europe/Warsaw"))
                .toLocalDate();
        String day = date.toString();
        String month = day.substring(0, 7);

        incrementAllTimeSeries(type.redisPrefix(), entityId, day, month);

        if (type == MetricType.SONG_PLAY && artistId != null) {
            String artistPrefix = "artist:plays:" + artistId;
            incrementAllTimeSeries(artistPrefix, artistId, day, month);
        }
    }

    private void incrementAllTimeSeries(String prefix, String memberId, String day, String month) {
        List<String> keys = List.of(
                prefix + ":" + day,
                prefix + ":" + month,
                prefix + ":all"
        );

        keys.forEach(key ->
                redis.opsForZSet().incrementScore(key, memberId, 1)
        );
    }
}
