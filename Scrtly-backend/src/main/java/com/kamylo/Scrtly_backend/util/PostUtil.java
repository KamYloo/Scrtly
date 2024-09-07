package com.kamylo.Scrtly_backend.util;

import com.kamylo.Scrtly_backend.model.Like;
import com.kamylo.Scrtly_backend.model.Post;
import com.kamylo.Scrtly_backend.model.User;

public class PostUtil {
    public final static boolean islikedbyReqUser(User reqUser, Post post) {
        for (Like like : post.getLikes()) {
            if (like.getUser().getId().equals(reqUser.getId())) {
                return true;
            }
        }
        return false;
    }
}
