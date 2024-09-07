package com.kamylo.Scrtly_backend.util;

import com.kamylo.Scrtly_backend.model.Comment;
import com.kamylo.Scrtly_backend.model.Like;
import com.kamylo.Scrtly_backend.model.Post;
import com.kamylo.Scrtly_backend.model.User;

public class CommentUtil {
    public final static boolean islikedbyReqUser(User reqUser, Comment comment) {
        for (Like like : comment.getLikes()) {
            if (like.getUser().getId().equals(reqUser.getId())) {
                return true;
            }
        }
        return false;
    }
}
