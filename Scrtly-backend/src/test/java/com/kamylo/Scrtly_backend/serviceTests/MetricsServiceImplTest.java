package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.album.mapper.AlbumMapper;
import com.kamylo.Scrtly_backend.album.repository.AlbumRepository;
import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.artist.mapper.ArtistMapper;
import com.kamylo.Scrtly_backend.artist.repository.ArtistRepository;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.metrics.service.MetricsServiceImpl;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.song.mapper.SongMapper;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsServiceImplTest {

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ZSetOperations<String, String> zSetOps;

    @Mock private ArtistRepository artistRepo;
    @Mock private SongRepository songRepo;
    @Mock private AlbumRepository albumRepo;

    @Mock private ArtistMapper artistMapper;
    @Mock private SongMapper songMapper;
    @Mock private AlbumMapper albumMapper;

    @InjectMocks private MetricsServiceImpl metricsService;

    private final String artistPrefix = "artist:views";
    private final String songPrefix = "song:plays";
    private final String albumPrefix = "album:views";

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);

        lenient().when(artistRepo.findAllById(anyList())).thenAnswer(inv -> {
            @SuppressWarnings("unchecked")
            List<Long> ids = inv.getArgument(0);
            List<ArtistEntity> list = new ArrayList<>();
            for (Long id : ids) {
                ArtistEntity a = new ArtistEntity();
                a.setId(id);
                list.add(a);
            }
            return list;
        });

        lenient().when(songRepo.findAllById(anyList())).thenAnswer(inv -> {
            @SuppressWarnings("unchecked")
            List<Long> ids = inv.getArgument(0);
            List<SongEntity> list = new ArrayList<>();
            for (Long id : ids) {
                SongEntity s = new SongEntity();
                s.setId(id);
                list.add(s);
            }
            return list;
        });

        lenient().when(albumRepo.findAllById(anyList())).thenAnswer(inv -> {
            @SuppressWarnings("unchecked")
            List<Integer> ids = inv.getArgument(0);
            List<AlbumEntity> list = new ArrayList<>();
            for (Integer id : ids) {
                AlbumEntity a = new AlbumEntity();
                a.setId(id);
                list.add(a);
            }
            return list;
        });

        lenient().when(artistMapper.toDto(any(ArtistEntity.class))).thenAnswer(inv -> {
            ArtistEntity e = inv.getArgument(0);
            ArtistDto dto = new ArtistDto();
            dto.setId(e.getId());
            return dto;
        });
        lenient().when(songMapper.toDto(any(SongEntity.class))).thenAnswer(inv -> {
            SongEntity e = inv.getArgument(0);
            SongDto dto = new SongDto();
            dto.setId(e.getId());
            return dto;
        });
        lenient().when(albumMapper.toDto(any(AlbumEntity.class))).thenAnswer(inv -> {
            AlbumEntity e = inv.getArgument(0);
            AlbumDto dto = new AlbumDto();
            dto.setId(e.getId());
            return dto;
        });
    }


    private String dayKey(String prefix) {
        return prefix + ":" + LocalDate.now();
    }

    private String monthKey(String prefix) {
        return prefix + ":" + LocalDate.now().toString().substring(0, 7);
    }

    private String allKey(String prefix) {
        return prefix + ":all";
    }

    @Test
    void getTopArtists_returnsMappedDtos_inRedisOrder() {
        int n = 2;
        LinkedHashSet<String> redisIds = new LinkedHashSet<>(List.of("3", "1"));
        when(zSetOps.reverseRange(eq(dayKey(artistPrefix)), eq(0L), eq((long) n - 1))).thenReturn(redisIds);

        List<ArtistDto> result = metricsService.getTopArtists("day", n);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(1L, result.get(1).getId());

        verify(zSetOps).reverseRange(eq(dayKey(artistPrefix)), eq(0L), eq((long) n - 1));
    }

    @Test
    void getTopSongs_fallsBack_fromDayToMonth_whenDayHasTooFew() {
        int n = 3;
        Set<String> daySet = Collections.emptySet();
        LinkedHashSet<String> monthSet = new LinkedHashSet<>(List.of("21", "22", "23"));
        when(zSetOps.reverseRange(eq(dayKey(songPrefix)), eq(0L), eq((long) n - 1))).thenReturn(daySet);
        when(zSetOps.reverseRange(eq(monthKey(songPrefix)), eq(0L), eq((long) n - 1))).thenReturn(monthSet);

        List<com.kamylo.Scrtly_backend.song.web.dto.SongDto> result = metricsService.getTopSongs("day", n);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(21L, result.get(0).getId());
        assertEquals(22L, result.get(1).getId());
        assertEquals(23L, result.get(2).getId());

        verify(zSetOps).reverseRange(eq(dayKey(songPrefix)), eq(0L), eq((long) n - 1));
        verify(zSetOps).reverseRange(eq(monthKey(songPrefix)), eq(0L), eq((long) n - 1));
    }

    @Test
    void getTopAlbums_usesIntegerIds_andFiltersMissingEntities() {
        int n = 3;
        LinkedHashSet<String> albumIds = new LinkedHashSet<>(List.of("5","6","7"));
        when(zSetOps.reverseRange(eq(allKey(albumPrefix)), eq(0L), eq((long) n - 1))).thenReturn(albumIds);

        List<com.kamylo.Scrtly_backend.album.web.dto.AlbumDto> result = metricsService.getTopAlbums("all", n);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(5, result.get(0).getId());
        assertEquals(6, result.get(1).getId());
        assertEquals(7, result.get(2).getId());

        verify(zSetOps).reverseRange(eq(allKey(albumPrefix)), eq(0L), eq((long) n - 1));
    }

    @Test
    void getTopSongs_returnsEmpty_whenRedisEmpty() {
        int n = 5;
        when(zSetOps.reverseRange(eq(allKey(songPrefix)), eq(0L), eq((long) n - 1))).thenReturn(Collections.emptySet());

        List<SongDto> result = metricsService.getTopSongs("all", n);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(zSetOps).reverseRange(eq(allKey(songPrefix)), eq(0L), eq((long) n - 1));
    }

    @Test
    void getTopAlbums_day_returns_day_when_day_has_enough() {
        int n = 2;
        LinkedHashSet<String> dayIds = new LinkedHashSet<>(List.of("5", "6"));
        when(zSetOps.reverseRange(eq(dayKey(albumPrefix)), eq(0L), eq((long) n - 1))).thenReturn(dayIds);

        List<AlbumDto> result = metricsService.getTopAlbums("day", n);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5, result.get(0).getId());
        assertEquals(6, result.get(1).getId());

        verify(zSetOps, never()).reverseRange(eq(monthKey(albumPrefix)), anyLong(), anyLong());
        verify(zSetOps, never()).reverseRange(eq(allKey(albumPrefix)), anyLong(), anyLong());
    }

    @Test
    void getTopAlbums_day_month_all_fallbacks_to_all_when_needed() {
        int n = 4;
        LinkedHashSet<String> dayIds = new LinkedHashSet<>(List.of("10", "11"));
        when(zSetOps.reverseRange(eq(dayKey(albumPrefix)), eq(0L), eq((long) n - 1))).thenReturn(dayIds);

        LinkedHashSet<String> monthIds = new LinkedHashSet<>(List.of("20"));
        when(zSetOps.reverseRange(eq(monthKey(albumPrefix)), eq(0L), eq((long) n - 1))).thenReturn(monthIds);

        LinkedHashSet<String> allIds = new LinkedHashSet<>(List.of("30","31","32","33"));
        when(zSetOps.reverseRange(eq(allKey(albumPrefix)), eq(0L), eq((long) n - 1))).thenReturn(allIds);

        List<AlbumDto> result = metricsService.getTopAlbums("day", n);

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(30, result.get(0).getId());
        assertEquals(31, result.get(1).getId());
        assertEquals(32, result.get(2).getId());
        assertEquals(33, result.get(3).getId());

        InOrder inOrder = inOrder(zSetOps);
        inOrder.verify(zSetOps).reverseRange(eq(dayKey(albumPrefix)), eq(0L), eq((long) n - 1));
        inOrder.verify(zSetOps).reverseRange(eq(monthKey(albumPrefix)), eq(0L), eq((long) n - 1));
        inOrder.verify(zSetOps).reverseRange(eq(allKey(albumPrefix)), eq(0L), eq((long) n - 1));
    }

    @Test
    void getTopAlbums_month_returns_month_when_enough() {
        int n = 2;
        LinkedHashSet<String> monthIds = new LinkedHashSet<>(List.of("40","41"));
        when(zSetOps.reverseRange(eq(monthKey(albumPrefix)), eq(0L), eq((long) n - 1))).thenReturn(monthIds);

        List<AlbumDto> result = metricsService.getTopAlbums("month", n);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(40, result.get(0).getId());
        assertEquals(41, result.get(1).getId());

        verify(zSetOps, never()).reverseRange(eq(allKey(albumPrefix)), anyLong(), anyLong());
    }

    @Test
    void getTopAlbums_all_returns_all_even_if_less_than_n() {
        int n = 5;
        LinkedHashSet<String> allIds = new LinkedHashSet<>(List.of("50","51"));
        when(zSetOps.reverseRange(eq(allKey(albumPrefix)), eq(0L), eq((long) n - 1))).thenReturn(allIds);

        List<AlbumDto> result = metricsService.getTopAlbums("all", n);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(50, result.get(0).getId());
        assertEquals(51, result.get(1).getId());
    }

    @Test
    void getTopArtists_day_returns_day_and_skips_month() {
        int n = 2;
        LinkedHashSet<String> dayIds = new LinkedHashSet<>(List.of("7","8"));
        when(zSetOps.reverseRange(eq(dayKey(artistPrefix)), eq(0L), eq((long) n - 1))).thenReturn(dayIds);

        List<ArtistDto> result = metricsService.getTopArtists("day", n);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(7L, result.get(0).getId());
        assertEquals(8L, result.get(1).getId());

        verify(zSetOps, never()).reverseRange(eq(monthKey(artistPrefix)), anyLong(), anyLong());
        verify(zSetOps, never()).reverseRange(eq(allKey(artistPrefix)), anyLong(), anyLong());
    }
}
