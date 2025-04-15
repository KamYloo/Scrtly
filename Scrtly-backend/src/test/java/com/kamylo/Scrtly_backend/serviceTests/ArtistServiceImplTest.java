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
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.service.FileService;
import com.kamylo.Scrtly_backend.service.UserRoleService;
import com.kamylo.Scrtly_backend.service.UserService;
import com.kamylo.Scrtly_backend.service.impl.ArtistServiceImpl;
import com.kamylo.Scrtly_backend.utils.ArtistUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
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
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ArtistServiceImplTest {

    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private SongRepository songRepository;
    @Mock
    private UserRepository userRepository;
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
        artistMapper = new ArtistMapperImpl(new ModelMapper());
        songMapper = new SongMapperImpl(new ModelMapper());
        artistService = new ArtistServiceImpl(
                artistRepository,
                userRepository,
                songRepository,
                userService,
                userRoleService,
                artistMapper,
                songMapper,
                fileService
        );
    }

    @Test
    void getArtists_shouldReturnArtists() {
        Pageable pageable = PageRequest.of(0, 10);
        ArtistEntity artistEntity = new ArtistEntity();
        ArtistDto expectedDto = artistMapper.mapTo(artistEntity);
        Page<ArtistEntity> artistPage = new PageImpl<>(Collections.singletonList(artistEntity));

        when(artistRepository.findAll(pageable)).thenReturn(artistPage);

        Page<ArtistDto> result = artistService.getArtists(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(expectedDto, result.getContent().get(0));
    }

    @Test
    void getArtistById_shouldReturnArtist_whenArtistExists() {
        Long artistId = 1L;
        ArtistEntity artistEntity = new ArtistEntity();

        UserEntity user = new UserEntity();
        user.setFollowers(new HashSet<>());
        artistEntity.setUser(user);

        ArtistDto expectedDto = artistMapper.mapTo(artistEntity);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));

        ArtistDto result = artistService.getArtistById(artistId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
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
        UserEntity artistUser = new UserEntity();
        artistUser.setId(2L);
        artistEntity.setUser(artistUser);
        ArtistDto expectedDto = artistMapper.mapTo(artistEntity);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));
        when(userService.findUserByEmail(username)).thenReturn(userEntity);

        try (MockedStatic<ArtistUtil> artistUtilMock = Mockito.mockStatic(ArtistUtil.class)) {
            artistUtilMock.when(() -> ArtistUtil.isArtistFollowed(artistEntity.getUser(), userEntity.getId()))
                    .thenReturn(true);

            ArtistDto result = artistService.getArtistProfile(artistId, username);
            assertNotNull(result);
            assertTrue(result.isObserved());
        }
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

    @Test
    void searchArtistsByName_shouldReturnArtists() {
        String artistName = "Test Artist";
        ArtistEntity artistEntity = new ArtistEntity();
        ArtistDto expectedDto = artistMapper.mapTo(artistEntity);
        Set<ArtistEntity> artistEntities = new HashSet<>(Collections.singletonList(artistEntity));

        when(artistRepository.findByPseudonym(artistName)).thenReturn(artistEntities);

        Set<ArtistDto> result = artistService.searchArtistsByName(artistName);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(expectedDto));
    }

    @Test
    void updateArtist_shouldUpdateArtist_whenUserIsArtist() throws Exception {
        String username = "artist@example.com";
        MultipartFile bannerImg = mock(MultipartFile.class);
        String newArtistBio = "New Bio";
        String oldBannerPath = "old/path/to/banner";
        String newBannerPath = "new/path/to/banner";

        // Budujemy encję artysty powiązaną z użytkownikiem
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setArtistBio("Old Bio");
        artistEntity.setBannerImg(oldBannerPath);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setArtistEntity(artistEntity);

        ArtistDto expectedDto = artistMapper.mapTo(artistEntity);
        expectedDto.setArtistBio(newArtistBio);
        expectedDto.setBannerImg(newBannerPath);

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(userService.findUserByEmail(username)).thenReturn(userEntity);
        when(fileService.updateFile(bannerImg, oldBannerPath, "artistBanners/")).thenReturn(newBannerPath);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        ArtistDto result = artistService.updateArtist(username, bannerImg, newArtistBio);

        assertNotNull(result);
        assertEquals(newArtistBio, artistEntity.getArtistBio());
        assertEquals(newBannerPath, artistEntity.getBannerImg());
        assertEquals(expectedDto, result);
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
    void updateArtist_shouldUpdateOnlyBio_whenBannerIsNull() throws Exception {
        String username = "artist@example.com";
        MultipartFile bannerImg = null;
        String newArtistBio = "Updated Bio";
        String currentBanner = "old/path/to/banner";

        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setArtistBio("Old Bio");
        artistEntity.setBannerImg(currentBanner);
        UserEntity userEntity = new UserEntity();
        userEntity.setArtistEntity(artistEntity);

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(userService.findUserByEmail(username)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        ArtistDto result = artistService.updateArtist(username, bannerImg, newArtistBio);

        assertNotNull(result);
        assertEquals(newArtistBio, artistEntity.getArtistBio());
        assertEquals(currentBanner, artistEntity.getBannerImg());
    }

    @Test
    void updateArtist_shouldUpdateOnlyBanner_whenBioIsNull() throws Exception {
        String username = "artist@example.com";
        MultipartFile bannerImg = mock(MultipartFile.class);
        String currentBanner = "old/path/to/banner";
        String newBannerPath = "new/path/to/banner";

        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setBannerImg(currentBanner);
        artistEntity.setArtistBio("Existing Bio");
        UserEntity userEntity = new UserEntity();
        userEntity.setArtistEntity(artistEntity);

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(userService.findUserByEmail(username)).thenReturn(userEntity);
        when(bannerImg.isEmpty()).thenReturn(false);
        when(fileService.updateFile(bannerImg, currentBanner, "artistBanners/")).thenReturn(newBannerPath);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        ArtistDto result = artistService.updateArtist(username, bannerImg, null);

        assertNotNull(result);
        assertEquals(newBannerPath, artistEntity.getBannerImg());
        assertEquals("Existing Bio", artistEntity.getArtistBio());
    }

    @Test
    void updateArtist_shouldNotUpdateAnything_whenBothAreNull() throws Exception {
        String username = "artist@example.com";
        MultipartFile bannerImg = null;
        String artistBio = null;
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setArtistBio("Old Bio");
        artistEntity.setBannerImg("old/path/to/banner");
        UserEntity userEntity = new UserEntity();
        userEntity.setArtistEntity(artistEntity);

        when(userRoleService.isArtist(username)).thenReturn(true);
        when(userService.findUserByEmail(username)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        ArtistDto result = artistService.updateArtist(username, bannerImg, artistBio);

        assertNotNull(result);
        assertEquals("Old Bio", artistEntity.getArtistBio());
        assertEquals("old/path/to/banner", artistEntity.getBannerImg());
    }

    @Test
    void getArtistTracks_shouldReturnTracks() {
        Long artistId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        ArtistEntity artistEntity = new ArtistEntity();

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));

        SongEntity songEntity = new SongEntity();
        SongDto expectedSongDto = songMapper.mapTo(songEntity);
        Page<SongEntity> songPage = new PageImpl<>(Collections.singletonList(songEntity));

        when(songRepository.findByArtistId(artistId, pageable)).thenReturn(songPage);

        Page<SongDto> result = artistService.getArtistTracks(artistId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(expectedSongDto, result.getContent().get(0));
    }


}
