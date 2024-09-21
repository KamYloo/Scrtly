package com.kamylo.Scrtly_backend.util;

import com.kamylo.Scrtly_backend.model.Album;
import com.kamylo.Scrtly_backend.model.Artist;

public class AlbumUtil {
    public static final boolean isReqArtistAlbum(Artist artist, Album album) {
        return album.getArtist().getId().equals(artist.getId());
    }
}
