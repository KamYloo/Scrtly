package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.AlbumDto;
import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.entity.AlbumEntity;
import com.kamylo.Scrtly_backend.entity.ArtistEntity;
import com.kamylo.Scrtly_backend.entity.SongEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.AlbumRepository;
import com.kamylo.Scrtly_backend.repository.ArtistRepository;
import com.kamylo.Scrtly_backend.repository.SongRepository;
import com.kamylo.Scrtly_backend.service.*;
import com.kamylo.Scrtly_backend.specification.AlbumSpecification;
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
    private final ArtistRepository artistRepository;
    private final UserRoleService userRoleService;
    private final FileService fileService;
    private final Mapper<AlbumEntity, AlbumDto> albumMapper;
    private final Mapper<SongEntity, SongDto> songMapper;
    private final SongRepository songRepository;

    @Override
    @Transactional
    public AlbumDto createAlbum(String title, MultipartFile albumImage, String username) {
        if (!userRoleService.isArtist(username)) {
            throw new CustomException(BusinessErrorCodes.ARTIST_UNAUTHORIZED);
        }
        ArtistEntity artist = (ArtistEntity) userService.findUserByEmail(username);

        String imagePath = null;
        if (!albumImage.isEmpty()) {
            imagePath = fileService.saveFile(albumImage, "albumImages/");
        }

        AlbumEntity album = AlbumEntity.builder()
                .title(title)
                .artist(artist)
                .coverImage(imagePath)
                .build();

        AlbumEntity savedAlbum = albumRepository.save(album);
        return albumMapper.mapTo(savedAlbum);
    }

    @Override
    public Page<AlbumDto> getAlbums(Pageable pageable) {
        return albumRepository.findAll(pageable).map(albumMapper::mapTo);
    }

    @Override
    public AlbumDto getAlbum(Integer albumId) {
        AlbumEntity album = albumRepository.findById(albumId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.ALBUM_NOT_FOUND));
        return albumMapper.mapTo(album);
    }

    @Override
    public Page<AlbumDto> searchAlbums(String artistName, String albumName, Pageable pageable) {
        Specification<AlbumEntity> spec = Specification
                .where(AlbumSpecification.artistContains(artistName))
                .and(AlbumSpecification.titleContains(albumName));

        return albumRepository.findAll(spec, pageable).map(albumMapper::mapTo);
    }

    @Override
    public List<AlbumDto> getAlbumsByArtist(Long artistId) {
        ArtistEntity artistEntity = artistRepository.findById(artistId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.ARTIST_NOT_FOUND));
        return albumRepository.findByArtistId(artistEntity.getId()).stream().map(albumMapper::mapTo).toList();
    }

    @Override
    public List<SongDto> getAlbumTracks(Integer albumId) {
        AlbumEntity albumEntity = albumMapper.mapFrom(getAlbum(albumId));
        if (albumEntity == null) {
            throw new CustomException(BusinessErrorCodes.ALBUM_NOT_FOUND);
        }
        return songRepository.findByAlbumId(albumEntity.getId()).stream().map(songMapper::mapTo).toList();
    }

    @Override
    @Transactional
    public void deleteAlbum(Integer albumId, String username) {
        if (!userRoleService.isArtist(username)) {
            throw new CustomException(BusinessErrorCodes.ARTIST_UNAUTHORIZED);
        }

        AlbumEntity albumEntity =albumRepository.findById(albumId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.ALBUM_NOT_FOUND));
        ArtistEntity artist = (ArtistEntity) userService.findUserByEmail(username);

        if (!artist.getId().equals(albumEntity.getArtist().getId())) {
            throw new CustomException(BusinessErrorCodes.ARTIST_MISMATCH);
        }
        albumEntity.getSongs()
                .forEach(song -> {
                    fileService.deleteFile(song.getTrack());
                    fileService.deleteFile(song.getImageSong());
                });
        fileService.deleteFile(albumEntity.getCoverImage());
        albumRepository.deleteById(albumId);
    }


}
