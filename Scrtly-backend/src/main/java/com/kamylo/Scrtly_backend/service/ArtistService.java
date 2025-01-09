package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.ArtistDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface ArtistService {
    Page<ArtistDto> getArtists(Pageable pageable);
    ArtistDto getArtistById(Long artistId);
    ArtistDto getArtistProfile(Long artistId, String username);
    Set<ArtistDto> searchArtistsByName(String artistName);
    ArtistDto updateArtist(String username, MultipartFile bannerImg, String artistBio);
    Page<SongDto> getArtistTracks(Long artistId, Pageable pageable);
}
