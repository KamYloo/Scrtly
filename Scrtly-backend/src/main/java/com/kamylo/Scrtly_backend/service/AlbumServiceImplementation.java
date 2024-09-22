package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.AlbumException;
import com.kamylo.Scrtly_backend.exception.ArtistException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Album;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.AlbumRepository;
import com.kamylo.Scrtly_backend.request.AlbumRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AlbumServiceImplementation implements AlbumService{
    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserService userService;

    @Override
    public Album createAlbum(AlbumRequest albumRequest) throws ArtistException, UserException {
        User user = userService.findUserById(albumRequest.getArtist().getId());
        if(user instanceof Artist artist) {
            Album album = new Album();
            album.setArtist(artist);
            album.setTitle(albumRequest.getTitle());
            album.setCoverImage(albumRequest.getCoverImage());
            album.setReleaseDate(LocalDate.now());

            return albumRepository.save(album);
        }
        else {
            throw new ArtistException("User is not an artist");
        }
    }

    @Override
    public List<Album> getAllAlbums() {
       return albumRepository.findAllByOrderByIdDesc();
    }

    @Override
    public List<Song> getAlbumTracks(Integer albumId) throws AlbumException {
        Album album = getAlbum(albumId);
        List<Song> songs = album.getSongs();
        return (songs != null) ? songs : new ArrayList<>();
    }

    @Override
    public Album getAlbum(Integer albumId) throws AlbumException {
       return albumRepository.findById(albumId).orElseThrow(() -> new AlbumException("Album not found with id " + albumId));
    }

    @Override
    public void deleteAlbum(Integer albumId, Long artistId) throws AlbumException, ArtistException {
        Album album = getAlbum(albumId);
        if (!artistId.equals(album.getArtist().getId())) {
            throw new ArtistException("Artist id mismatch");
        }
        albumRepository.deleteById(albumId);
    }
}
