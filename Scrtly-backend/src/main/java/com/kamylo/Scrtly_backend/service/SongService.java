package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.model.Album;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.Song;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

@Service
public interface SongService {
 Song createSong(String title, Album album, Artist artist, MultipartFile imageSong, MultipartFile audioFile) throws IOException, UnsupportedAudioFileException;
}
