package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.AlbumException;
import com.kamylo.Scrtly_backend.exception.ArtistException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.Album;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.request.AlbumRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface AlbumService {
    Album createAlbum(AlbumRequest albumRequest, MultipartFile albumImage) throws UserException, ArtistException;
    List<Album> getAllAlbums();
    List<Album> getAlbumsByArtist(Long artistId) throws UserException, ArtistException;
    List<Song> getAlbumTracks(Integer albumId) throws AlbumException;
    Album getAlbum(Integer albumId) throws AlbumException;
    void deleteAlbum(Integer albumId, Long artistId) throws AlbumException, ArtistException;
}
