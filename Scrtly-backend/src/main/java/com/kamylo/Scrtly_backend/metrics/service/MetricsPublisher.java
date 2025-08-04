package com.kamylo.Scrtly_backend.metrics.service;

import com.kamylo.Scrtly_backend.metrics.domain.MetricEvent;
import com.kamylo.Scrtly_backend.metrics.domain.enums.MetricType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MetricsPublisher {
    private final RabbitTemplate rabbit;

    @Value("${metrics.exchange}")
    private String exchange;
    @Value("${metrics.routing.view}")
    private String routingView;
    @Value("${metrics.routing.play}")
    private String routingPlay;
    @Value("${metrics.routing.album}")
    private String routingAlbum;

    public MetricsPublisher(
            @Qualifier("metricsRabbitTemplate") RabbitTemplate rabbit
    ) {
        this.rabbit = rabbit;
    }

    public void publishArtistView(Long artistId) {
        rabbit.convertAndSend(
                exchange,
                routingView,
                new MetricEvent(MetricType.ARTIST_VIEW.eventName(), artistId, null, Instant.now())
        );
    }

    public void publishSongPlay(Long songId, Long artistId) {
        rabbit.convertAndSend(
                exchange,
                routingPlay,
                new MetricEvent(
                        MetricType.SONG_PLAY.eventName(),
                        songId,
                        artistId,
                        Instant.now()
                )
        );
    }

    public void publishAlbumView(Integer albumId) {
        rabbit.convertAndSend(
                exchange,
                routingAlbum,
                new MetricEvent(MetricType.ALBUM_VIEW.eventName(), albumId.longValue(), null, Instant.now())
        );
    }
}
