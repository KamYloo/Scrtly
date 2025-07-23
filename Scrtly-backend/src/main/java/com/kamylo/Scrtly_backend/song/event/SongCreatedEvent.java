package com.kamylo.Scrtly_backend.song.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SongCreatedEvent extends ApplicationEvent {
    private final Long songId;
    private final String localAudioPath;

    public SongCreatedEvent(Object source, Long songId, String localAudioPath) {
        super(source);
        this.songId = songId;
        this.localAudioPath = localAudioPath;
    }
}