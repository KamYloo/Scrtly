package com.kamylo.Scrtly_backend.song.service;

import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.song.web.dto.request.SongRequest;
import org.springframework.stereotype.Service;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Set;

@Service
public interface SongService {
 SongDto createSong(SongRequest songRequest, String username) throws IOException, UnsupportedAudioFileException;
 Set<SongDto> searchSongByTitle(String title);
 void deleteSong(Long songId, String username);
}
