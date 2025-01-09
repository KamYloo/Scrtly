package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.AlbumDto;
import com.kamylo.Scrtly_backend.dto.SongDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface AlbumService {
    AlbumDto createAlbum(String title, MultipartFile albumImage, String username);
    Page<AlbumDto> getAlbums(Pageable pageable);
    AlbumDto getAlbum(Integer albumId) ;
    Page<AlbumDto> searchAlbums(String artistName, String albumName, Pageable pageable);
    List<AlbumDto> getAlbumsByArtist(Long artistId);
    List<SongDto> getAlbumTracks(Integer albumId);
    void deleteAlbum(Integer albumId, String username) ;
}
