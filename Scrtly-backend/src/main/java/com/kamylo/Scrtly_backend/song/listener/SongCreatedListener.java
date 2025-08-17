package com.kamylo.Scrtly_backend.song.listener;

import com.kamylo.Scrtly_backend.song.event.SongCreatedEvent;
import com.kamylo.Scrtly_backend.song.service.HlsService;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
@RequiredArgsConstructor
public class SongCreatedListener {

    private final HlsService hlsService;
    private final SongRepository songRepository;

    @Async("hlsExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSongCreated(SongCreatedEvent evt) {
        String manifestUrl = hlsService.generateHls(evt.getLocalAudioPath(), evt.getSongId());

        songRepository.findById(evt.getSongId()).ifPresent(song -> {
            song.setHlsManifestUrl(manifestUrl);
            songRepository.save(song);
        });
    }
}