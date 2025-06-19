package com.kamylo.Scrtly_backend.song.service.impl;

import com.kamylo.Scrtly_backend.album.service.AlbumService;
import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.common.service.FileService;
import com.kamylo.Scrtly_backend.song.service.HlsService;
import com.kamylo.Scrtly_backend.song.service.SongService;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.song.web.dto.SongRequest;
import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.user.service.UserRoleService;
import com.kamylo.Scrtly_backend.user.service.UserService;
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
    private final FileService fileService;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final AlbumService albumService;
    private final Mapper<AlbumEntity, AlbumDto> albumMapper;
    private final Mapper<SongEntity, SongDto> songMapper;
    private final HlsService hlsService;

    @Value("${application.file.image-dir}")
    private String storageBasePath;

    @Value("${application.file.cdn}")
    private String cdnBaseUrl;

    @Override
    @Transactional
    public SongDto createSong(SongRequest songRequest, String username, MultipartFile imageSong, MultipartFile audioFile) throws IOException, UnsupportedAudioFileException {
        validateArtistOrAdmin(username);

        UserEntity artist = userService.findUserByEmail(username);
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
                srcAudio = audioPath.replace(cdnBaseUrl + "audio/", "");
            }
            duration = getAudioDuration(new File("/uploads/audio/" + srcAudio));
        }

        SongEntity songEntity = SongEntity.builder()
                .title(songRequest.getTitle())
                .album(album)
                .artist(artist)
                .imageSong(imagePath)
                .track(audioPath)
                .duration(duration)
                .contentType(audioFile.getContentType())
                .build();

        SongEntity savedSong = songRepository.save(songEntity);

        String localAudioPath = savedSong.getTrack()
                .replace(cdnBaseUrl + "audio/", storageBasePath + "audio/");

        hlsService.generateHls(localAudioPath, savedSong.getId())
                .thenAccept(manifestUrl -> {
                    savedSong.setHlsManifestUrl(manifestUrl);
                    songRepository.save(savedSong);
                });

        return songMapper.mapTo(savedSong);
    }

    @Override
    public Set<SongDto> searchSongByTitle(String title) {
        return songRepository.findByTitle(title)
                .stream()
                .map(songMapper::mapTo)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void deleteSong(Long songId, String username) {
        validateArtistOrAdmin(username);
        SongEntity songEntity = songRepository.findById(songId)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.SONG_NOT_FOUND));

        if (validateSongOwnership(username, songEntity)) {
            hlsService.deleteHlsFolder(songEntity.getId());
            fileService.deleteFile(songEntity.getImageSong());
            fileService.deleteFile(songEntity.getTrack());
            songRepository.delete(songEntity);
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

    private void validateArtistOrAdmin(String username) {
        if (!(userRoleService.isArtist(username) || userRoleService.isAdmin(username))) {
            throw new CustomException(BusinessErrorCodes.ARTIST_UNAUTHORIZED);
        }
    }

    private boolean validateSongOwnership(String username, SongEntity song) {
         if (userRoleService.isAdmin(username)) {
            return true;
        }
        UserEntity user = userService.findUserByEmail(username);
        return user.getId().equals(song.getArtist().getId());
    }
}
