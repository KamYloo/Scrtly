package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.model.Album;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.Song;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class SongServiceImplementation implements SongService {
    @Autowired
    private SongRepository songRepository;

    @Override
    public Song createSong(String title, Album album, Artist artist, MultipartFile imageSong, MultipartFile audioFile) throws IOException, UnsupportedAudioFileException {
       Song song = new Song();
       song.setTitle(title);
       song.setAlbum(album);
       song.setArtist(artist);

       if (!imageSong.isEmpty()) {
           String imagePath = saveImage(imageSong);
           song.setImageSong("/uploads/songImages/" + imagePath);
       }

        if (!audioFile.isEmpty()) {
            String audioPath = saveAudio(audioFile);
            song.setTrack("/uploads/audio/" + audioPath);
            int duration = getAudioDuration(new File("src/main/resources/static/uploads/audio/" + audioPath));
            song.setDuration(duration);
        }
        return songRepository.save(song);
    }

    private String saveImage(MultipartFile imageSong) throws IOException {
        try {

            Path folderPath = Paths.get("src/main/resources/static/uploads/songImages");

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            String fileName = UUID.randomUUID().toString() + "_" + imageSong.getOriginalFilename();
            Path filePath = folderPath.resolve(fileName);
            Files.copy(imageSong.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error saving image file.", e);
        }
    }

    private String saveAudio(MultipartFile audioFile) throws IOException {
        try {
            Path folderPath = Paths.get("src/main/resources/static/uploads/audio");

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            String fileName = UUID.randomUUID().toString() + "_" + audioFile.getOriginalFilename();
            Path filePath = folderPath.resolve(fileName);
            Files.copy(audioFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error saving audio file.", e);
        }
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
