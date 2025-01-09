package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.AlbumDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.entity.*;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.SongRepository;
import com.kamylo.Scrtly_backend.request.SongRequest;
import com.kamylo.Scrtly_backend.service.AlbumService;
import com.kamylo.Scrtly_backend.service.SongService;
import com.kamylo.Scrtly_backend.service.UserRoleService;
import com.kamylo.Scrtly_backend.service.UserService;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final FileServiceImpl fileService;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final AlbumService albumService;
    private final Mapper<AlbumEntity, AlbumDto> albumMapper;
    private final Mapper<SongEntity, SongDto> songMapper;

    @Value("${application.file.cdn}")
    private String cdnBaseUrl;

    @Override
    @Transactional
    public SongDto createSong(SongRequest songRequest, String username, MultipartFile imageSong, MultipartFile audioFile) throws IOException, UnsupportedAudioFileException {
        if (!userRoleService.isArtist(username)) {
            throw new CustomException(BusinessErrorCodes.ARTIST_UNAUTHORIZED);
        }

        ArtistEntity artist = (ArtistEntity) userService.findUserByEmail(username);
        AlbumEntity album = albumMapper.mapFrom(albumService.getAlbum(songRequest.getAlbumId()));

        String imagePath = null;
        if (!imageSong.isEmpty()) {
            imagePath = fileService.saveFile(imageSong, "songImages/");
        }

        String audioPath = null;
        int duration = 0;
        if (!audioFile.isEmpty()) {
            audioPath = fileService.saveFile(audioFile, "audio/");
            String srcAudio = "";
            if (audioPath.startsWith(cdnBaseUrl)) {
                srcAudio = audioPath.replace(cdnBaseUrl+"audio/", "");
            }
            duration = getAudioDuration(new File("uploads/audio/" + srcAudio));
        }

        SongEntity songEntity = SongEntity.builder()
                .title(songRequest.getTitle())
                .album(album)
                .artist(artist)
                .imageSong(imagePath)
                .track(audioPath)
                .duration(duration)
                .build();

        SongEntity savedSong = songRepository.save(songEntity);
        return songMapper.mapTo(savedSong);
    }

    @Override
    public Set<SongDto> searchSongByTitle(String title) {
        return songRepository.findByTitle(title).stream().map(songMapper::mapTo).collect(Collectors.toSet());
    }

    @Override
    public void deleteSong(Long songId, String username) {

        SongEntity songEntity = songRepository.findById(songId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.SONG_NOT_FOUND));

        if (validateSongOwnership(username, songEntity)) {
            songRepository.delete(songEntity);
            fileService.deleteFile(songEntity.getImageSong());
            fileService.deleteFile(songEntity.getTrack());
        } else {
            throw new CustomException(BusinessErrorCodes.SONG_MISMATCH);
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

    private boolean validateSongOwnership(String username, SongEntity song) {
        if (!userRoleService.isArtist(username)) {
            throw new CustomException(BusinessErrorCodes.ARTIST_UNAUTHORIZED);
        }
        ArtistEntity artist = (ArtistEntity) userService.findUserByEmail(username);
        return artist.getId().equals(song.getArtist().getId());
    }
}
