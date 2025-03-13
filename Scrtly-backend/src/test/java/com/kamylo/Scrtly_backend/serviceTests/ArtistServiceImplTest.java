package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import com.kamylo.Scrtly_backend.entity.ArtistEntity;
import com.kamylo.Scrtly_backend.entity.SongEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.ArtistMapperImpl;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.mappers.SongMapperImpl;
import com.kamylo.Scrtly_backend.repository.ArtistRepository;
import com.kamylo.Scrtly_backend.repository.SongRepository;
import com.kamylo.Scrtly_backend.service.FileService;
import com.kamylo.Scrtly_backend.service.UserRoleService;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.service.impl.ArtistServiceImpl;
import com.kamylo.Scrtly_backend.utils.ArtistUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArtistServiceImplTest {

    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private SongRepository songRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private FileService fileService;

    private Mapper<ArtistEntity, ArtistDto> artistMapper;
    private Mapper<SongEntity, SongDto> songMapper;

    @InjectMocks
    private ArtistServiceImpl artistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        artistMapper = new ArtistMapperImpl(new ModelMapper());
        songMapper = new SongMapperImpl(new ModelMapper());
        artistService = new ArtistServiceImpl(
                artistRepository, songRepository, userService,
                userRoleService, artistMapper, songMapper, fileService
        );
    }

    @Test
    void getArtists_shouldReturnArtists() {
        Pageable pageable = PageRequest.of(0, 10);
        ArtistEntity artistEntity = new ArtistEntity();
        ArtistDto artistDto = new ArtistDto();
        Page<ArtistEntity> artistPage = new PageImpl<>(Collections.singletonList(artistEntity));

        when(artistRepository.findAll(pageable)).thenReturn(artistPage);

        Page<ArtistDto> result = artistService.getArtists(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(artistDto, result.getContent().get(0));
    }

    @Test
    void getArtistById_shouldReturnArtist_whenArtistExists() {
        Long artistId = 1L;
        ArtistEntity artistEntity = new ArtistEntity();
        ArtistDto artistDto = new ArtistDto();

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));

        ArtistDto result = artistService.getArtistById(artistId);

        assertNotNull(result);
        assertEquals(artistDto, result);
    }

    @Test
    void getArtistById_shouldThrowException_whenArtistDoesNotExist() {
        Long artistId = 1L;

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> artistService.getArtistById(artistId));
        assertEquals(BusinessErrorCodes.ARTIST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getArtistProfile_shouldReturnArtistProfile() {
        Long artistId = 1L;
        String username = "user@example.com";
        ArtistEntity artistEntity = new ArtistEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        ArtistDto artistDto = new ArtistDto();

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));
        when(userService.findUserByEmail(username)).thenReturn(userEntity);
        // Mock the ArtistUtil.isArtistFollowed method
        mockStatic(ArtistUtil.class);
        when(ArtistUtil.isArtistFollowed(artistEntity, userEntity.getId())).thenReturn(true);

        ArtistDto result = artistService.getArtistProfile(artistId, username);

        assertNotNull(result);
        assertTrue(result.isObserved());
    }

    @Test
    void searchArtistsByName_shouldReturnArtists() {
        String artistName = "Test Artist";
        ArtistEntity artistEntity = new ArtistEntity();
        ArtistDto artistDto = new ArtistDto();
        Set<ArtistEntity> artistEntities = new HashSet<>(Collections.singletonList(artistEntity));

        when(artistRepository.findByArtistName(artistName)).thenReturn(artistEntities);

        Set<ArtistDto> result = artistService.searchArtistsByName(artistName);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(artistDto));
    }

    @Test
    void updateArtist_shouldUpdateArtist_whenUserIsArtist() {
        String username = "artist@example.com";
        MultipartFile bannerImg = mock(MultipartFile.class);
        String artistBio = "New Bio";
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setBannerImg("old/path/to/banner");
        ArtistDto artistDto = new ArtistDto();
        artistDto.setArtistBio("New Bio");
        artistDto.setBannerImg("new/path/to/banner");

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(userService.findUserByEmail(username)).thenReturn(artistEntity);
        when(fileService.updateFile(bannerImg, "old/path/to/banner", "artistBanners/")).thenReturn("new/path/to/banner");
        when(artistRepository.save(artistEntity)).thenReturn(artistEntity);

        ArtistDto result = artistService.updateArtist(username, bannerImg, artistBio);

        assertNotNull(result);
        assertEquals(artistDto, result);
        assertEquals("New Bio", artistEntity.getArtistBio());
        assertEquals("new/path/to/banner", artistEntity.getBannerImg());
    }

    @Test
    void updateArtist_shouldThrowException_whenUserIsNotArtist() {
        String username = "user@example.com";
        MultipartFile bannerImg = mock(MultipartFile.class);
        String artistBio = "New Bio";

        when(userRoleService.isArtist(username)).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class,
                () -> artistService.updateArtist(username, bannerImg, artistBio));
        assertEquals(BusinessErrorCodes.ARTIST_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    void getArtistTracks_shouldReturnTracks() {
        Long artistId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        ArtistEntity artistEntity = new ArtistEntity();
        SongEntity songEntity = new SongEntity();
        SongDto songDto = new SongDto();
        Page<SongEntity> songPage = new PageImpl<>(Collections.singletonList(songEntity));

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));
        when(songRepository.findByArtistId(artistId, pageable)).thenReturn(songPage);

        Page<SongDto> result = artistService.getArtistTracks(artistId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(songDto, result.getContent().get(0));
    }

    @Test
    void updateArtist_shouldUpdateOnlyBio_whenBannerIsNull() {
        String username = "artist@example.com";
        MultipartFile bannerImg = null;
        String artistBio = "Updated Bio";
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setBannerImg("old/path/to/banner");
        artistEntity.setArtistBio("Old Bio");

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(userService.findUserByEmail(username)).thenReturn(artistEntity);
        when(artistRepository.save(any(ArtistEntity.class))).thenReturn(artistEntity);

        ArtistDto result = artistService.updateArtist(username, bannerImg, artistBio);

        assertNotNull(result);
        assertEquals(artistBio, result.getArtistBio());
        assertEquals("old/path/to/banner", result.getBannerImg());
    }

    @Test
    void updateArtist_shouldUpdateOnlyBanner_whenBioIsNull() {
        String username = "artist@example.com";
        MultipartFile bannerImg = mock(MultipartFile.class);
        String artistBio = null;
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setBannerImg("old/path/to/banner");

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(userService.findUserByEmail(username)).thenReturn(artistEntity);
        when(fileService.updateFile(bannerImg, "old/path/to/banner", "artistBanners/")).thenReturn("new/path/to/banner");
        when(artistRepository.save(any(ArtistEntity.class))).thenReturn(artistEntity);

        ArtistDto result = artistService.updateArtist(username, bannerImg, artistBio);

        assertNotNull(result);
        assertEquals("new/path/to/banner", result.getBannerImg());
    }

    @Test
    void updateArtist_shouldNotUpdateAnything_whenBothAreNull() {
        String username = "artist@example.com";
        MultipartFile bannerImg = null;
        String artistBio = null;
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setBannerImg("old/path/to/banner");
        artistEntity.setArtistBio("Old Bio");

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(userService.findUserByEmail(username)).thenReturn(artistEntity);
        when(artistRepository.save(any(ArtistEntity.class))).thenReturn(artistEntity);

        ArtistDto result = artistService.updateArtist(username, bannerImg, artistBio);

        assertNotNull(result);
        assertEquals("Old Bio", result.getArtistBio());
        assertEquals("old/path/to/banner", result.getBannerImg());
    }

    @Test
    void getArtistProfile_shouldThrowException_whenArtistDoesNotExist() {
        Long artistId = 1L;
        String username = "user@example.com";

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> artistService.getArtistProfile(artistId, username));

        assertEquals(BusinessErrorCodes.ARTIST_NOT_FOUND, exception.getErrorCode());
    }

}