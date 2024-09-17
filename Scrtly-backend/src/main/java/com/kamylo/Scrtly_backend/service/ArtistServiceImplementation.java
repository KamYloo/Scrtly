package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.ArtistException;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
