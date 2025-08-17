package com.kamylo.Scrtly_backend.metrics.service;

import com.kamylo.Scrtly_backend.album.web.dto.AlbumDto;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistDto;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import java.util.List;

public interface MetricsService {
    List<ArtistDto> getTopArtists(String window, int n);
    List<SongDto> getTopSongs(String window, int n);
    List<AlbumDto> getTopAlbums(String window, int n);
}
