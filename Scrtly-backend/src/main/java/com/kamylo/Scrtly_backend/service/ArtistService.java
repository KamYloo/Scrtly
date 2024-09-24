package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.AlbumException;
import com.kamylo.Scrtly_backend.exception.ArtistException;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface ArtistService {
    List<Artist> getAllArtists();
    Artist getArtistById(Long artistId) throws ArtistException;
    Set<Artist> searchArtistsByName(String artistName) throws ArtistException;
    Artist updateArtist(Long artistId, String bannerImg, String artistBio) throws ArtistException;
    List<Song> getArtistTracks(Long artistId) throws ArtistException;
}
