package com.kamylo.Scrtly_backend.metrics.service;

import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import com.kamylo.Scrtly_backend.album.repository.AlbumRepository;
import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.artist.repository.ArtistRepository;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {
    private final StringRedisTemplate redis;
    private final ArtistRepository artistRepo;
    private final SongRepository songRepo;
    private final AlbumRepository albumRepo;
    private final Mapper<ArtistEntity, ArtistDto> artistMapper;
    private final Mapper<SongEntity, SongDto> songMapper;
    private final Mapper<AlbumEntity, AlbumDto> albumMapper;

    @Override
    public List<ArtistDto> getTopArtists(String window, int n) {
        String base = switch (window.toLowerCase()) {
            case "day" -> LocalDate.now().toString();
            case "month" -> LocalDate.now().toString().substring(0, 7);
            default -> "all";
        };
        String key = "artist:views:" + base;
        return fetchLong(key, n, artistRepo, artistMapper, ArtistEntity::getId);
    }

    @Override
    public List<SongDto> getTopSongs(String window, int n) {
        String base = switch (window.toLowerCase()) {
            case "day" -> LocalDate.now().toString();
            case "month" -> LocalDate.now().toString().substring(0, 7);
            default -> "all";
        };
        String key = "song:plays:" + base;
        return fetchLong(key, n, songRepo, songMapper, SongEntity::getId);
    }

    @Override
    public List<AlbumDto> getTopAlbums(String window, int n) {
        String base = switch (window.toLowerCase()) {
            case "day"   -> LocalDate.now().toString();
            case "month" -> LocalDate.now().toString().substring(0, 7);
            default      -> "all";
        };
        String key = "album:views:" + base;
        return fetchInt(key, n, albumRepo, albumMapper, AlbumEntity::getId);
    }

    private <E,D> List<D> fetchLong(
            String key,
            int n,
            JpaRepository<E,Long> repo,
            Mapper<E,D> mapper,
            Function<E,Long> idExtractor) {

        Set<String> ids = redis.opsForZSet().reverseRange(key, 0, n-1);
        if (ids == null || ids.isEmpty()) return List.of();

        List<Long> longIds = ids.stream()
                .map(Long::valueOf)
                .toList();

        List<E> entities = repo.findAllById(longIds);
        Map<Long,E> map = entities.stream()
                .collect(Collectors.toMap(idExtractor, e->e));

        return longIds.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(mapper::mapTo)
                .toList();
    }

    private <E,D> List<D> fetchInt(
            String key,
            int n,
            JpaRepository<E,Integer> repo,
            Mapper<E,D> mapper,
            Function<E,Integer> idExtractor) {

        Set<String> ids = redis.opsForZSet().reverseRange(key, 0, n-1);
        if (ids == null || ids.isEmpty()) return List.of();

        List<Integer> intIds = ids.stream()
                .map(Integer::valueOf)
                .toList();

        List<E> entities = repo.findAllById(intIds);
        Map<Integer,E> map = entities.stream()
                .collect(Collectors.toMap(idExtractor, e->e));

        return intIds.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(mapper::mapTo)
                .toList();
    }
}
