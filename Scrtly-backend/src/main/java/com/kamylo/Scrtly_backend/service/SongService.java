package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.request.SongRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Set;

@Service
public interface SongService {
 SongDto createSong(SongRequest songRequest, String username, MultipartFile imageSong, MultipartFile audioFile) throws IOException, UnsupportedAudioFileException;
 Set<SongDto> searchSongByTitle(String title);
 void deleteSong(Long songId, String username);
}
