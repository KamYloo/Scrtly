package com.kamylo.Scrtly_backend.util;

import com.kamylo.Scrtly_backend.model.Album;
import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.User;

public class AlbumUtil {
    public static final boolean isReqArtistAlbum(User user, Album album) {
        return album.getArtist().getId().equals(user.getId());
    }
}
