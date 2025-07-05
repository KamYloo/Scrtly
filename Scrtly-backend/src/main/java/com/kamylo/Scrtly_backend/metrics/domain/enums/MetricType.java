package com.kamylo.Scrtly_backend.metrics.domain.enums;

import java.util.Arrays;

public enum MetricType {
    ARTIST_VIEW("artistView", "artist:views"),
    SONG_PLAY(  "songPlay",   "song:plays"),
    ALBUM_VIEW( "albumView",  "album:views");

    private final String eventName;
    private final String redisPrefix;

    MetricType(String eventName, String redisPrefix) {
        this.eventName   = eventName;
        this.redisPrefix = redisPrefix;
    }

    public String eventName() {
        return eventName;
    }

    public String redisPrefix() {
        return redisPrefix;
    }

    public static MetricType fromEvent(String ev) {
        return Arrays.stream(values())
                .filter(t -> t.eventName.equals(ev))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown metric: " + ev));
    }
}
