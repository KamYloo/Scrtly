package com.kamylo.Scrtly_backend.util;

import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.User;

public class ArtistUtil {
    public static  final boolean isReqArtist(User reqUser, Artist artist){
        return reqUser.getId().equals(artist.getId());
    }
}
