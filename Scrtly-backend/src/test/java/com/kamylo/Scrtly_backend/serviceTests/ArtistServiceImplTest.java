package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.artist.mapper.ArtistMapper;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.song.mapper.SongMapper;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.artist.repository.ArtistRepository;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.kamylo.Scrtly_backend.common.service.FileService;
import com.kamylo.Scrtly_backend.user.service.UserRoleService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.artist.service.impl.ArtistServiceImpl;
import com.kamylo.Scrtly_backend.common.utils.ArtistUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.MockedStatic;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class ArtistServiceImplTest {

    private static final Long ARTIST_ID = 1L;
    private static final String USER_EMAIL = "user@example.com";
    private static final String ARTIST_USERNAME = "artist@example.com";

    @Mock private ArtistRepository artistRepository;
    @Mock private SongRepository songRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserService userService;
    @Mock private UserRoleService userRoleService;
    @Mock private FileService fileService;
    @Mock private ArtistMapper artistMapper;
    @Mock private SongMapper songMapper;

    @InjectMocks private ArtistServiceImpl artistService;

    private ArtistEntity artistEntity;
    private UserEntity artistUser;
    private SongEntity songEntity;

    @BeforeEach
    void setUp() {
        artistUser = UserEntity.builder()
                .id(2L)
                .email("artist-owner@example.com")
                .build();

        artistEntity = ArtistEntity.builder()
                .id(ARTIST_ID)
                .pseudonym("Pseudonym")
                .user(artistUser)
                .build();

        artistUser.setArtistEntity(artistEntity);

        songEntity = SongEntity.builder()
                .id(10L)
                .title("Track 1")
                .build();

        Mockito.lenient().when(artistMapper.toDto(any(ArtistEntity.class))).thenAnswer(inv -> {
            ArtistEntity a = inv.getArgument(0);
            ArtistDto d = new ArtistDto();
            d.setId(a.getId());
            d.setPseudonym(a.getPseudonym());
            return d;
        });

        Mockito.lenient().when(songMapper.toDto(any(SongEntity.class))).thenAnswer(inv -> {
            SongEntity s = inv.getArgument(0);
            SongDto sd = new SongDto();
            sd.setTitle(s.getTitle());
            return sd;
        });
    }

    @Test
    void getArtists_shouldReturnPagedDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ArtistEntity> page = new PageImpl<>(Collections.singletonList(artistEntity));
        when(artistRepository.findAll(pageable)).thenReturn(page);

        Page<ArtistDto> res = artistService.getArtists(pageable);

        assertNotNull(res);
        assertEquals(1, res.getTotalElements());
        assertEquals("Pseudonym", res.getContent().get(0).getPseudonym());
        verify(artistRepository).findAll(pageable);
        verify(artistMapper).toDto(any(ArtistEntity.class));
    }

    @Nested
    class GetByIdTests {
        @Test
        void getArtistById_existing_returnsDto() {
            when(artistRepository.findById(ARTIST_ID)).thenReturn(Optional.of(artistEntity));

            ArtistDto dto = artistService.getArtistById(ARTIST_ID);

            assertNotNull(dto);
            assertEquals(ARTIST_ID, dto.getId());
            assertEquals("Pseudonym", dto.getPseudonym());
            verify(artistRepository).findById(ARTIST_ID);
            verify(artistMapper).toDto(artistEntity);
        }

        @Test
        void getArtistById_missing_throws() {
            when(artistRepository.findById(ARTIST_ID)).thenReturn(Optional.empty());

            CustomException ex = assertThrows(CustomException.class, () -> artistService.getArtistById(ARTIST_ID));
            assertEquals(BusinessErrorCodes.ARTIST_NOT_FOUND, ex.getErrorCode());
            verify(artistRepository).findById(ARTIST_ID);
        }
    }

    @Nested
    class GetArtistProfileTests {
        @Test
        void getArtistProfile_whenUsernameNull_setsObservedFalse() {
            when(artistRepository.findById(ARTIST_ID)).thenReturn(Optional.of(artistEntity));

            ArtistDto dto = artistService.getArtistProfile(ARTIST_ID, null);

            assertNotNull(dto);
            assertFalse(dto.isObserved());
            verify(artistRepository).findById(ARTIST_ID);
            verify(artistMapper).toDto(artistEntity);
            verifyNoInteractions(userService);
        }

        @Test
        void getArtistProfile_whenUsernameProvided_andFollowed_setsObservedTrue() {
            String username = USER_EMAIL;
            UserEntity user = UserEntity.builder().id(5L).email(username).build();

            when(artistRepository.findById(ARTIST_ID)).thenReturn(Optional.of(artistEntity));
            when(userService.findUserByEmail(username)).thenReturn(user);

            try (MockedStatic<ArtistUtil> mocked = mockStatic(ArtistUtil.class)) {
                mocked.when(() -> ArtistUtil.isArtistFollowed(artistEntity.getUser(), user.getId())).thenReturn(true);

                ArtistDto dto = artistService.getArtistProfile(ARTIST_ID, username);

                assertNotNull(dto);
                assertTrue(dto.isObserved());
                verify(artistRepository).findById(ARTIST_ID);
                verify(userService).findUserByEmail(username);
                mocked.verify(() -> ArtistUtil.isArtistFollowed(artistEntity.getUser(), user.getId()));
            }
        }

        @Test
        void getArtistProfile_whenUsernameProvided_andNotFollowed_setsObservedFalse() {
            String username = USER_EMAIL;
            UserEntity user = UserEntity.builder().id(6L).email(username).build();

            when(artistRepository.findById(ARTIST_ID)).thenReturn(Optional.of(artistEntity));
            when(userService.findUserByEmail(username)).thenReturn(user);

            try (MockedStatic<ArtistUtil> mocked = mockStatic(ArtistUtil.class)) {
                mocked.when(() -> ArtistUtil.isArtistFollowed(artistEntity.getUser(), user.getId())).thenReturn(false);

                ArtistDto dto = artistService.getArtistProfile(ARTIST_ID, username);

                assertNotNull(dto);
                assertFalse(dto.isObserved());
                verify(userService).findUserByEmail(username);
                mocked.verify(() -> ArtistUtil.isArtistFollowed(artistEntity.getUser(), user.getId()));
            }
        }

        @Test
        void getArtistProfile_missingArtist_throws() {
            when(artistRepository.findById(ARTIST_ID)).thenReturn(Optional.empty());

            CustomException ex = assertThrows(CustomException.class, () -> artistService.getArtistProfile(ARTIST_ID, USER_EMAIL));
            assertEquals(BusinessErrorCodes.ARTIST_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Test
    void searchArtistsByName_returnsDtos() {
        String name = "SomeName";
        ArtistEntity a = ArtistEntity.builder().id(2L).pseudonym(name).build();
        Set<ArtistEntity> set = new HashSet<>();
        set.add(a);

        when(artistRepository.findByPseudonym(name)).thenReturn(set);

        Set<ArtistDto> result = artistService.searchArtistsByName(name);

        assertNotNull(result);
        assertEquals(1, result.size());
        ArtistDto dto = result.iterator().next();
        assertEquals(name, dto.getPseudonym());
        verify(artistRepository).findByPseudonym(name);
    }

    @Test
    void searchArtistsByName_empty_returnsEmpty() {
        String name = "NoOne";
        when(artistRepository.findByPseudonym(name)).thenReturn(Collections.emptySet());

        Set<ArtistDto> result = artistService.searchArtistsByName(name);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(artistRepository).findByPseudonym(name);
    }

    @Nested
    class UpdateArtistTests {

        @Test
        void updateArtist_updatesBannerAndBio_whenUserIsArtist_andBannerNotEmpty() {
            String username = ARTIST_USERNAME;
            MultipartFile bannerImg = mock(MultipartFile.class);
            String newBio = "New bio";
            String oldBanner = "old/banner.jpg";
            String newBanner = "new/banner.jpg";

            ArtistEntity a = ArtistEntity.builder()
                    .id(999L)
                    .pseudonym("PS")
                    .bannerImg(oldBanner)
                    .artistBio("Old bio")
                    .build();

            UserEntity user = UserEntity.builder().id(20L).email(username).artistEntity(a).build();

            when(userRoleService.isArtist(username)).thenReturn(true);
            when(userService.findUserByEmail(username)).thenReturn(user);
            when(bannerImg.isEmpty()).thenReturn(false);
            when(fileService.updateFile(bannerImg, oldBanner, "artistBanners/")).thenReturn(newBanner);
            when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            ArtistDto dto = artistService.updateArtist(username, bannerImg, newBio);

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(captor.capture());
            UserEntity savedUser = captor.getValue();

            assertEquals(newBio, savedUser.getArtistEntity().getArtistBio());
            assertEquals(newBanner, savedUser.getArtistEntity().getBannerImg());
            assertEquals("PS", dto.getPseudonym());

            verify(userRoleService).isArtist(username);
            verify(userService).findUserByEmail(username);
            verify(fileService).updateFile(bannerImg, oldBanner, "artistBanners/");
            verify(artistMapper).toDto(savedUser.getArtistEntity());
        }

        @Test
        void updateArtist_updatesOnlyBio_whenBannerNull() {
            String username = ARTIST_USERNAME;
            MultipartFile bannerImg = null;
            String newBio = "Bio updated";

            ArtistEntity a = ArtistEntity.builder()
                    .id(1000L)
                    .pseudonym("PB")
                    .artistBio("Old")
                    .bannerImg("keep.jpg")
                    .build();

            UserEntity user = UserEntity.builder().id(21L).email(username).artistEntity(a).build();

            when(userRoleService.isArtist(username)).thenReturn(true);
            when(userService.findUserByEmail(username)).thenReturn(user);
            when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            ArtistDto dto = artistService.updateArtist(username, bannerImg, newBio);

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(captor.capture());
            assertEquals(newBio, captor.getValue().getArtistEntity().getArtistBio());
            assertEquals("keep.jpg", captor.getValue().getArtistEntity().getBannerImg());

            verify(fileService, never()).updateFile(any(), anyString(), anyString());
            verify(artistMapper).toDto(any(ArtistEntity.class));
        }

        @Test
        void updateArtist_doesNotChangeBanner_whenBannerEmpty() {
            String username = ARTIST_USERNAME;
            MultipartFile bannerImg = mock(MultipartFile.class);
            when(bannerImg.isEmpty()).thenReturn(true);

            ArtistEntity a = ArtistEntity.builder()
                    .id(1001L)
                    .pseudonym("PB2")
                    .bannerImg("unchanged.jpg")
                    .artistBio("bio")
                    .build();

            UserEntity user = UserEntity.builder().id(22L).email(username).artistEntity(a).build();

            when(userRoleService.isArtist(username)).thenReturn(true);
            when(userService.findUserByEmail(username)).thenReturn(user);
            when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            ArtistDto dto = artistService.updateArtist(username, bannerImg, null);

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(captor.capture());
            assertEquals("unchanged.jpg", captor.getValue().getArtistEntity().getBannerImg());
            verify(fileService, never()).updateFile(any(), anyString(), anyString());
        }

        @Test
        void updateArtist_throws_whenUserNotArtist() {
            String username = "not-artist@example.com";
            MultipartFile bannerImg = mock(MultipartFile.class);

            when(userRoleService.isArtist(username)).thenReturn(false);

            CustomException ex = assertThrows(CustomException.class, () -> artistService.updateArtist(username, bannerImg, "bio"));
            assertEquals(BusinessErrorCodes.ARTIST_UNAUTHORIZED, ex.getErrorCode());
            verify(userService, never()).findUserByEmail(anyString());
            verify(userRepository, never()).save(any());
            verify(fileService, never()).updateFile(any(), anyString(), anyString());
        }

        @Test
        void updateArtist_propagatesFileServiceException() {
            String username = ARTIST_USERNAME;
            MultipartFile bannerImg = mock(MultipartFile.class);
            when(bannerImg.isEmpty()).thenReturn(false);

            ArtistEntity a = ArtistEntity.builder()
                    .id(777L)
                    .pseudonym("PB3")
                    .bannerImg("old.jpg")
                    .build();

            UserEntity user = UserEntity.builder().id(33L).email(username).artistEntity(a).build();

            when(userRoleService.isArtist(username)).thenReturn(true);
            when(userService.findUserByEmail(username)).thenReturn(user);
            when(fileService.updateFile(bannerImg, "old.jpg", "artistBanners/")).thenThrow(new RuntimeException("IO fail"));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> artistService.updateArtist(username, bannerImg, null));
            assertEquals("IO fail", ex.getMessage());

            verify(userRepository, never()).save(any());
        }
    }

    @Test
    void getArtistTracks_returnsPagedSongDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        when(artistRepository.findById(ARTIST_ID)).thenReturn(Optional.of(artistEntity));

        Page<SongEntity> page = new PageImpl<>(Collections.singletonList(songEntity));
        when(songRepository.findByArtistId(ARTIST_ID, pageable)).thenReturn(page);

        Page<SongDto> res = artistService.getArtistTracks(ARTIST_ID, pageable);

        assertNotNull(res);
        assertEquals(1, res.getTotalElements());
        assertEquals(songEntity.getTitle(), res.getContent().get(0).getTitle());

        verify(artistRepository).findById(ARTIST_ID);
        verify(songRepository).findByArtistId(ARTIST_ID, pageable);
        verify(songMapper).toDto(any(SongEntity.class));
    }

    @Test
    void getArtistTracks_whenArtistMissing_throws() {
        Pageable pageable = PageRequest.of(0, 10);
        when(artistRepository.findById(ARTIST_ID)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> artistService.getArtistTracks(ARTIST_ID, pageable));
        assertEquals(BusinessErrorCodes.ARTIST_NOT_FOUND, ex.getErrorCode());

        verify(artistRepository).findById(ARTIST_ID);
        verifyNoInteractions(songRepository);
    }
}
