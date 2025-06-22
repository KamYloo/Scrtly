package com.kamylo.Scrtly_backend.common.utils;

import com.kamylo.Scrtly_backend.user.domain.UserEntity;


public class ArtistUtil {

    public static boolean isArtistFollowed(UserEntity artist, Long userId) {
        return artist.getFollowers() != null && artist.getFollowers().stream()
                .anyMatch(follower -> follower.getId().equals(userId));
    }
}

