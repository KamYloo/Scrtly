package com.kamylo.Scrtly_backend.album.service;

import com.kamylo.Scrtly_backend.album.mapper.AlbumMapper;
import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.common.service.FileService;
import com.kamylo.Scrtly_backend.song.mapper.SongMapper;
import com.kamylo.Scrtly_backend.song.service.HlsService;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.album.repository.AlbumRepository;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.album.repository.AlbumSpecification;
import com.kamylo.Scrtly_backend.user.service.UserRoleService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final FileService fileService;
    private final AlbumMapper albumMapper;
    private final SongMapper songMapper;
    private final SongRepository songRepository;
    private final HlsService hlsService;

    @Override
    @Transactional
    public AlbumDto createAlbum(String title, MultipartFile albumImage, String username) {
        validateArtistOrAdmin(username);
        ArtistEntity artist = userService.findUserByEmail(username).getArtistEntity();

        String imagePath = null;
        if (albumImage != null && !albumImage.isEmpty()) {
            imagePath = fileService.saveFile(albumImage, "albumImages/");
        }

        AlbumEntity album = AlbumEntity.builder()
                .title(title)
                .artist(artist)
                .coverImage(imagePath)
                .build();

        AlbumEntity savedAlbum = albumRepository.save(album);
        return albumMapper.toDto(savedAlbum);
    }

    @Override
    public Page<AlbumDto> getAlbums(Pageable pageable) {
        return albumRepository.findAll(pageable).map(albumMapper::toDto);
    }

    @Override
    public AlbumDto getAlbum(Integer albumId) {
        AlbumEntity album = albumRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.ALBUM_NOT_FOUND));
        return albumMapper.toDto(album);
    }

    @Override
    public Page<AlbumDto> searchAlbums(String pseudonym, String albumName, Pageable pageable) {
        Specification<AlbumEntity> spec = Specification
                .where(AlbumSpecification.artistContains(pseudonym))
                .and(AlbumSpecification.titleContains(albumName));

        return albumRepository.findAll(spec, pageable).map(albumMapper::toDto);
    }

    @Override
    public Page<AlbumDto> getAlbumsByArtist(Long artistId, String albumName, Pageable pageable) {
        Page<AlbumEntity> page;
        if (albumName == null || albumName.trim().isEmpty()) {
            page = albumRepository.findByArtistId(artistId, pageable);
        } else {
            String trimmed = albumName.trim();
            page = albumRepository.findByArtistIdAndTitleIgnoreCaseContaining(artistId, trimmed, pageable);
        }

        return page.map(albumMapper::toDto);
    }

    @Override
    public List<SongDto> getAlbumTracks(Integer albumId) {
        AlbumEntity albumEntity = albumMapper.toEntity(getAlbum(albumId));
        if (albumEntity == null) {
            throw new CustomException(BusinessErrorCodes.ALBUM_NOT_FOUND);
        }
        return songRepository.findByAlbumId(albumEntity.getId())
                .stream()
                .map(songMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAlbum(Integer albumId, String username) {
        validateArtistOrAdmin(username);
        AlbumEntity albumEntity = albumRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.ALBUM_NOT_FOUND));
        UserEntity artist = userService.findUserByEmail(username);

        if (!artist.getId().equals(albumEntity.getArtist().getId()) && !userRoleService.isAdmin(username)) {
            throw new CustomException(BusinessErrorCodes.ARTIST_MISMATCH);
        }
        albumEntity.getSongs().forEach(song -> {
            fileService.deleteFile(song.getTrack());
            fileService.deleteFile(song.getImageSong());
            hlsService.deleteHlsFolder(song.getId());
        });
        fileService.deleteFile(albumEntity.getCoverImage());
        albumRepository.deleteById(albumId);
    }

    private void validateArtistOrAdmin(String username) {
        if (!(userRoleService.isArtist(username) || userRoleService.isAdmin(username))) {
            throw new CustomException(BusinessErrorCodes.ARTIST_UNAUTHORIZED);
        }
    }
}
