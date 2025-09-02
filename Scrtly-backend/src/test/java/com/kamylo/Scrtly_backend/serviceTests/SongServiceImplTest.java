package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.album.service.AlbumService;
import com.kamylo.Scrtly_backend.album.mapper.AlbumMapper;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.common.service.FileService;
import com.kamylo.Scrtly_backend.song.event.SongCreatedEvent;
import com.kamylo.Scrtly_backend.song.mapper.SongMapper;
import com.kamylo.Scrtly_backend.song.service.HlsService;
import com.kamylo.Scrtly_backend.song.service.impl.SongServiceImpl;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.song.web.dto.request.SongRequest;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.service.UserRoleService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongServiceImplTest {

    @Mock private SongRepository songRepository;
    @Mock private FileService fileService;
    @Mock private UserService userService;
    @Mock private UserRoleService userRoleService;
    @Mock private AlbumService albumService;
    @Mock private AlbumMapper albumMapper;
    @Mock private SongMapper songMapper;
    @Mock private HlsService hlsService;
    @Mock private ApplicationEventPublisher publisher;

    @InjectMocks private SongServiceImpl songService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(songService, "storageBasePath", "/files/");
        ReflectionTestUtils.setField(songService, "cdnBaseUrl", "http://cdn/");
    }

    @Test
    void createSong_success_withImageOnly() throws IOException, UnsupportedAudioFileException {
        SongRequest req = new SongRequest();
        req.setTitle("My Song");
        req.setAlbumId(42);

        when(userRoleService.isArtist("alice")).thenReturn(true);
        UserEntity alice = new UserEntity(); alice.setId(7L);
        when(userService.findUserByEmail("alice")).thenReturn(alice);

        when(albumService.getAlbum(42))
                .thenReturn(new com.kamylo.Scrtly_backend.album.web.dto.AlbumDto());
        when(albumMapper.toEntity(any()))
                .thenReturn(new AlbumEntity());

        MockMultipartFile image = new MockMultipartFile(
                "imageSong", "cover.jpg", "image/jpeg", new byte[]{1,2});
        MockMultipartFile audio = new MockMultipartFile(
                "audioFile", "song.mp3", "audio/mpeg", new byte[0]
        );
        req.setImageSong(image);
        req.setAudioFile(audio);

        when(fileService.saveFile(eq(image), contains("songImages")))
                .thenReturn("imgPath");

        SongEntity saved = SongEntity.builder()
                .id(99L)
                .title("My Song")
                .imageSong("imgPath")
                .track("http://cdn/audio/song.mp3")
                .duration(0)
                .contentType("audio/mpeg")
                .build();
        when(songRepository.save(any())).thenReturn(saved);

        SongDto dtoOut = new SongDto();
        when(songMapper.toDto(saved)).thenReturn(dtoOut);

        SongDto result = songService.createSong(req, "alice");

        assertSame(dtoOut, result);
        verify(fileService).saveFile(eq(image), contains("songImages"));
        verify(fileService, never()).saveFile(eq(audio), anyString());

        ArgumentCaptor<SongCreatedEvent> evCap =
                ArgumentCaptor.forClass(SongCreatedEvent.class);
        verify(publisher).publishEvent(evCap.capture());
        SongCreatedEvent ev = evCap.getValue();
        assertEquals(99L, ev.getSongId());
        assertTrue(ev.getLocalAudioPath().contains("/files/audio/song.mp3"));
    }

    @Test
    void createSong_success_withAudio_nonMp3_usesAudioSystem() throws Exception {
        SongRequest req = new SongRequest();
        req.setTitle("Audio Song");
        req.setAlbumId(1);

        MockMultipartFile image = new MockMultipartFile("imageSong", "i.jpg", "image/jpeg", new byte[]{1});
        MockMultipartFile audio = new MockMultipartFile("audioFile", "t.wav", "audio/wav", new byte[]{1,2,3});
        req.setImageSong(image);
        req.setAudioFile(audio);

        when(userRoleService.isArtist("bob")).thenReturn(true);
        UserEntity bob = new UserEntity(); bob.setId(3L);
        when(userService.findUserByEmail("bob")).thenReturn(bob);

        when(albumService.getAlbum(1)).thenReturn(new com.kamylo.Scrtly_backend.album.web.dto.AlbumDto());
        when(albumMapper.toEntity(any())).thenReturn(new AlbumEntity());

        when(fileService.saveFile(eq(image), contains("songImages"))).thenReturn("img");
        when(fileService.saveFile(eq(audio), contains("audio"))).thenReturn("http://cdn/audio/t.wav");

        SongEntity saved = SongEntity.builder()
                .id(11L)
                .track("http://cdn/audio/t.wav")
                .imageSong("img")
                .build();
        when(songRepository.save(any())).thenReturn(saved);
        when(songMapper.toDto(saved)).thenReturn(new SongDto());

        try (MockedStatic<AudioSystem> as = mockStatic(AudioSystem.class)) {
            AudioInputStream ais = mock(AudioInputStream.class);
            AudioFormat format = new AudioFormat(44100f, 16, 2, true, false);
            when(ais.getFormat()).thenReturn(format);
            when(ais.getFrameLength()).thenReturn(44100L * 5); // 5s
            as.when(() -> AudioSystem.getAudioInputStream(any(File.class))).thenReturn(ais);

            SongDto out = songService.createSong(req, "bob");

            assertNotNull(out);
            verify(fileService).saveFile(eq(audio), contains("audio"));
            verify(fileService).saveFile(eq(image), contains("songImages"));
        }
    }

    @Test
    void createSong_unauthorized_throws() {
        when(userRoleService.isArtist("bob")).thenReturn(false);
        when(userRoleService.isAdmin("bob")).thenReturn(false);

        SongRequest req = new SongRequest();
        req.setTitle("No rights");
        req.setAlbumId(1);

        CustomException ex = assertThrows(
                CustomException.class,
                () -> songService.createSong(req, "bob")
        );
        assertEquals(BusinessErrorCodes.ARTIST_UNAUTHORIZED, ex.getErrorCode());
    }

    @Test
    void deleteSong_success_asArtist() {
        // given
        long id = 5L;
        SongEntity song = new SongEntity();
        song.setId(id);
        song.setImageSong("imgPath");
        song.setTrack("audioPath");
        ArtistEntity art = new ArtistEntity();
        art.setId(7L);
        song.setArtist(art);

        when(userRoleService.isAdmin("u")).thenReturn(false);
        when(userRoleService.isArtist("u")).thenReturn(true);
        UserEntity u = new UserEntity(); u.setId(7L);
        when(userService.findUserByEmail("u")).thenReturn(u);
        when(songRepository.findById(id)).thenReturn(Optional.of(song));

        songService.deleteSong(id, "u");

        verify(hlsService).deleteHlsFolder(id);
        verify(fileService).deleteFile("imgPath");
        verify(fileService).deleteFile("audioPath");
        verify(songRepository).delete(song);
    }

    @Test
    void deleteSong_forbidden_throws() {
        long id = 8L;
        SongEntity song = new SongEntity();
        song.setId(id);
        com.kamylo.Scrtly_backend.artist.domain.ArtistEntity art =
                new com.kamylo.Scrtly_backend.artist.domain.ArtistEntity();
        art.setId(99L);
        song.setArtist(art);

        when(userRoleService.isArtist("x")).thenReturn(true);
        when(userRoleService.isAdmin("x")).thenReturn(false);
        when(songRepository.findById(id)).thenReturn(Optional.of(song));
        UserEntity x = new UserEntity(); x.setId(7L);
        when(userService.findUserByEmail("x")).thenReturn(x);

        CustomException ex = assertThrows(
                CustomException.class,
                () -> songService.deleteSong(id, "x")
        );
        assertEquals(BusinessErrorCodes.SONG_MISMATCH, ex.getErrorCode());
    }

    @Test
    void searchSongByTitle_mapsToDto() {
        SongEntity e = new SongEntity();
        when(songRepository.findByTitle("foo")).thenReturn(Set.of(e));
        when(songMapper.toDto(e)).thenReturn(new SongDto());

        Set<SongDto> out = songService.searchSongByTitle("foo");

        assertEquals(1, out.size());
        verify(songMapper).toDto(e);
    }

    @Test
    void getAudioDuration_nonMp3_usesAudioSystem() {
        File anyFile = new File("whatever.wav");

        try (MockedStatic<AudioSystem> as = mockStatic(AudioSystem.class)) {
            AudioInputStream ais = mock(AudioInputStream.class);
            AudioFormat format = new AudioFormat(48000f, 16, 2, true, false);
            when(ais.getFormat()).thenReturn(format);
            when(ais.getFrameLength()).thenReturn(48000L * 3);
            as.when(() -> AudioSystem.getAudioInputStream(any(File.class))).thenReturn(ais);

            Object result = ReflectionTestUtils.invokeMethod(songService, "getAudioDuration", anyFile);
            assertInstanceOf(Integer.class, result);
            assertEquals(3, result);
        }
    }

    @Test
    void getMP3Duration_calculates_using_bitstream_header() throws Exception {
        File temp = File.createTempFile("test-mp3-", ".mp3");
        temp.deleteOnExit();
        try (RandomAccessFile raf = new RandomAccessFile(temp, "rw")) {
            raf.setLength(16_000_000L);
        }

        Header headerMock = mock(Header.class);
        when(headerMock.bitrate()).thenReturn(128_000);

        try (MockedConstruction<Bitstream> mc = mockConstruction(Bitstream.class,
                (mock, ctx) -> when(mock.readFrame()).thenReturn(headerMock))) {

            Object res = ReflectionTestUtils.invokeMethod(songService, "getMP3Duration", temp);
            assertInstanceOf(Integer.class, res);
            assertEquals(1000, res);
        }
    }
}
