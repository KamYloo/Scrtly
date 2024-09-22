package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.AlbumException;
import com.kamylo.Scrtly_backend.exception.ArtistException;
import com.kamylo.Scrtly_backend.model.Album;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.Song;
import com.kamylo.Scrtly_backend.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ArtistServiceImplementation implements ArtistService {

    @Autowired
    ArtistRepository artistRepository;

    @Override
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    @Override
    public Artist getArtistById(Long artistId) throws ArtistException {
        return artistRepository.findById(artistId).orElseThrow(() -> new ArtistException("Artist not found with id " + artistId));
    }

    @Override
    public Set<Artist> searchArtistsByName(String artistName) throws ArtistException {
        return artistRepository.findByArtistName(artistName);
    }

    @Override
    public Artist updateArtist(Long artistId, String bannerImg, String artistBio) throws ArtistException {
       Artist artist = getArtistById(artistId);

       String currentBannerImg = artist.getBannerImg();

       if(currentBannerImg != null && !currentBannerImg.isEmpty()){
           Path oldFilePath = Paths.get("src/main/resources/static").resolve(currentBannerImg);
           File oldFile = oldFilePath.toFile();
           if (oldFile.exists()) {
               oldFile.delete();
           }
       }
       artist.setBannerImg(bannerImg);
       artist.setArtistBio(artistBio);
       return artistRepository.save(artist);
    }

    @Override
    public List<Song> getArtistTracks(Long artistId) throws ArtistException {
        Artist artist = getArtistById(artistId);
        List<Song> songs = artist.getSongs();
        return (songs != null) ? songs : new ArrayList<>();
    }
}
