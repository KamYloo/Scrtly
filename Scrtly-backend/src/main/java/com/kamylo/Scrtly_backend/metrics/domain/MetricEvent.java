package com.kamylo.Scrtly_backend.metrics.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class MetricEvent {
    private String type;
    private Long entityId;
    @Nullable
    private final Long artistId;
    private Instant timestamp;
}
