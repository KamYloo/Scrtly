package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.entity.ArtistEntity;
import com.kamylo.Scrtly_backend.entity.SongEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.ArtistRepository;
import com.kamylo.Scrtly_backend.repository.SongRepository;
import com.kamylo.Scrtly_backend.service.ArtistService;
import com.kamylo.Scrtly_backend.service.FileService;
import com.kamylo.Scrtly_backend.service.UserRoleService;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.utils.ArtistUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final Mapper<ArtistEntity, ArtistDto> artistMapper;
    private final Mapper<SongEntity, SongDto> songMapper;
    private final FileService fileService;

    @Override
    public Page<ArtistDto> getArtists(Pageable pageable) {
        return artistRepository.findAll(pageable).map(artistMapper::mapTo);
    }

    @Override
    public ArtistDto getArtistById(Long artistId) {
        ArtistEntity artistEntity = artistRepository.findById(artistId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.ARTIST_NOT_FOUND));
        return artistMapper.mapTo(artistEntity);
    }

    @Override
    public ArtistDto getArtistProfile(Long artistId, String username) {
        ArtistEntity artistEntity = artistRepository.findById(artistId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.ARTIST_NOT_FOUND));
        UserEntity user = userService.findUserByEmail(username);
        ArtistDto artistDto = artistMapper.mapTo(artistEntity);
        artistDto.setObserved(ArtistUtil.isArtistFollowed(artistEntity, user.getId()));
        return artistDto;
    }

    @Override
    public Set<ArtistDto> searchArtistsByName(String artistName) {
        Set<ArtistEntity> artists = artistRepository.findByArtistName(artistName);
        return artists.stream()
                .map(artistMapper::mapTo)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    @Transactional
    public ArtistDto updateArtist(String username, MultipartFile bannerImg, String artistBio) {
        if (!userRoleService.isArtist(username)) {
            throw new CustomException(BusinessErrorCodes.ARTIST_UNAUTHORIZED);
        }
        ArtistEntity artist = (ArtistEntity) userService.findUserByEmail(username);

        if (artistBio != null) {
            artist.setArtistBio(artistBio);
        }
        if (bannerImg != null && !bannerImg.isEmpty()) {
            String imagePath = fileService.updateFile(bannerImg, artist.getBannerImg(), "artistBanners/");
            artist.setBannerImg(imagePath);
        }

        return artistMapper.mapTo(artistRepository.save(artist));
    }

    @Override
    public Page<SongDto> getArtistTracks(Long artistId, Pageable pageable) {
       artistMapper.mapFrom(getArtistById(artistId));
       return songRepository.findByArtistId(artistId, pageable).map(songMapper::mapTo);
    }

}
