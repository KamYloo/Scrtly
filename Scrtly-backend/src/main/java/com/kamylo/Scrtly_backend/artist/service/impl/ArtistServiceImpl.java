package com.kamylo.Scrtly_backend.artist.service.impl;

import com.kamylo.Scrtly_backend.artist.service.ArtistService;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.artist.repository.ArtistRepository;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.kamylo.Scrtly_backend.common.service.FileService;
import com.kamylo.Scrtly_backend.user.service.UserRoleService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.common.utils.ArtistUtil;
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
    private final UserRepository userRepository;
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
        boolean observed = false;
        if (username != null) {
            UserEntity user = userService.findUserByEmail(username);
            observed = ArtistUtil.isArtistFollowed(artistEntity.getUser(), user.getId());
        }

        ArtistDto artistDto = artistMapper.mapTo(artistEntity);
        artistDto.setObserved(observed);
        return artistDto;
    }

    @Override
    public Set<ArtistDto> searchArtistsByName(String pseudonym) {
        Set<ArtistEntity> artists = artistRepository.findByPseudonym(pseudonym);
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
        UserEntity user = userService.findUserByEmail(username);

        ArtistEntity artistEntity = user.getArtistEntity();

        if (artistBio != null) {
            artistEntity.setArtistBio(artistBio);
        }
        if (bannerImg != null && !bannerImg.isEmpty()) {
            String imagePath = fileService.updateFile(bannerImg, artistEntity.getBannerImg(), "artistBanners/");
            artistEntity.setBannerImg(imagePath);
        }
        userRepository.save(user);
        return artistMapper.mapTo(artistEntity);
    }

    @Override
    public Page<SongDto> getArtistTracks(Long artistId, Pageable pageable) {
       artistMapper.mapFrom(getArtistById(artistId));
       return songRepository.findByArtistId(artistId, pageable).map(songMapper::mapTo);
    }

}
