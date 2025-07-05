package com.kamylo.Scrtly_backend.metrics.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricEvent {
    private String type;
    private Long entityId;
    private Instant timestamp;
}
