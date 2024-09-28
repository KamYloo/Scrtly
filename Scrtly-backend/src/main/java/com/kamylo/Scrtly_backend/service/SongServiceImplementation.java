package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.AlbumException;
import com.kamylo.Scrtly_backend.exception.ArtistException;
import com.kamylo.Scrtly_backend.exception.PlayListException;
import com.kamylo.Scrtly_backend.exception.SongException;
import com.kamylo.Scrtly_backend.model.Album;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.PlayList;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.repository.PlayListRepository;
import com.kamylo.Scrtly_backend.repository.SongRepository;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

@Service
public class SongServiceImplementation implements SongService {
    @Autowired
    private SongRepository songRepository;

    @Autowired
    private FileServiceImplementation fileService;

    @Override
    public Song createSong(String title, Album album, Artist artist, MultipartFile imageSong, MultipartFile audioFile) throws IOException, UnsupportedAudioFileException {
       Song song = new Song();
       song.setTitle(title);
       song.setAlbum(album);
       song.setArtist(artist);

       if (!imageSong.isEmpty()) {
           String imagePath = fileService.saveFile(imageSong, "/uploads/songImages");
           song.setImageSong("/uploads/songImages/" + imagePath);
       }

        if (!audioFile.isEmpty()) {
            String audioPath = fileService.saveFile(audioFile, "/uploads/audio");
            song.setTrack("/uploads/audio/" + audioPath);
            int duration = getAudioDuration(new File("src/main/resources/static/uploads/audio/" + audioPath));
            song.setDuration(duration);
        }
        return songRepository.save(song);
    }

    @Override
    public Song findSongById(Long songId) throws SongException {
        return songRepository.findById(songId).orElseThrow(() -> new SongException("Song not found with id " + songId));
    }

    @Override
    public Set<Song> searchSongByTitle(String title) {
        return songRepository.findByTitle(title);
    }

    @Override
    public void deleteSong(Long songId, Long artistId) throws SongException, ArtistException {
        Song song = findSongById(songId);
        if (song == null) {
            throw new SongException("Song not found with id " + songId);
        }

        if(!artistId.equals(song.getArtist().getId())) {
            throw new ArtistException("Artist id mismatch");
        }
        songRepository.deleteById(songId);
        fileService.deleteFile(song.getImageSong());
        fileService.deleteFile(song.getTrack());
    }

    private int getAudioDuration(File file) throws IOException, UnsupportedAudioFileException {
        if (file.getName().toLowerCase().endsWith(".mp3")) {
            return getMP3Duration(file);
        } else {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            double durationInSeconds = (frames + 0.0) / format.getFrameRate();
            return (int) Math.round(durationInSeconds);
        }
    }

    private int getMP3Duration(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            Bitstream bitstream = new Bitstream(fis);
            Header header = bitstream.readFrame();
            int duration = (int) (file.length() / (header.bitrate() / 8.0));
            bitstream.close();
            return duration;
        } catch (BitstreamException e) {
            throw new IOException("Failed to read MP3 file", e);
        }
    }
}
