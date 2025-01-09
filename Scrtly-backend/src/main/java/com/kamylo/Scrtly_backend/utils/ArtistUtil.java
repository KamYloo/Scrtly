package com.kamylo.Scrtly_backend.utils;

import com.kamylo.Scrtly_backend.entity.ArtistEntity;


public class ArtistUtil {

    public static boolean isArtistFollowed(ArtistEntity artist, Long userId) {
        return artist.getFollowers() != null && artist.getFollowers().stream()
                .anyMatch(follower -> follower.getId().equals(userId));
    }
}

