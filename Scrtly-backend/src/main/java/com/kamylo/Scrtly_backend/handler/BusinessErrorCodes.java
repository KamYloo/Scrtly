package com.kamylo.Scrtly_backend.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessErrorCodes {
    NO_CODE(0,"No code", HttpStatus.NOT_IMPLEMENTED),
    INCORRECT_CURRENT_PASSWORD(300, "Current password is incorrect", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_DOES_NOT_MATCH(301, "New password does not match", HttpStatus.BAD_REQUEST),
    BAD_CREDENTIALS(304, "Login and / or password is incorrect", HttpStatus.FORBIDDEN),
    BAD_COOKIE(305, "No jwt cookie found", HttpStatus.BAD_REQUEST),
    BAD_JWT_TOKEN(306, "Invalid JWT token", HttpStatus.BAD_REQUEST),
    EMAIL_IS_USED(307, "Email is used", HttpStatus.BAD_REQUEST),
    FOLLOW_ERROR(308, "You cannot follow yourself.", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_FOUND(309, "Image not found", HttpStatus.NOT_FOUND),
    IMAGE_FETCH_FAILED(310, "Failed to fetch image", HttpStatus.INTERNAL_SERVER_ERROR),
    ARTIST_UNAUTHORIZED(311, "User is not an artist", HttpStatus.UNAUTHORIZED),
    ARTIST_NOT_FOUND(312, "Artist not found", HttpStatus.NOT_FOUND),
    ALBUM_NOT_FOUND(313, "Album not found", HttpStatus.NOT_FOUND),
    ARTIST_MISMATCH(314, "Artist no permissions", HttpStatus.UNAUTHORIZED),
    POST_MISMATCH(315, "No user permissions for this post", HttpStatus.FORBIDDEN),
    POST_NOT_FOUND(316, "Post not found", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND(317, "Comment not found", HttpStatus.NOT_FOUND),
    COMMENT_MISMATCH(318, "No user permissions for this comment", HttpStatus.FORBIDDEN),
    SONG_NOT_FOUND(319, "Song not found", HttpStatus.NOT_FOUND),
    CHATROOM_NOT_FOUND(320, "ChatRoom not found", HttpStatus.NOT_FOUND),
    CHATROOM_MISMATCH(321, "No user permissions for this chatRoom", HttpStatus.FORBIDDEN),
    CHAT_MESSAGE_NOT_FOUND(322, "ChatMessage not found", HttpStatus.NOT_FOUND),
    CHAT_MESSAGE_MISMATCH(323, "No user permissions for this chatMessage", HttpStatus.FORBIDDEN),
    CHAT_MESSAGE_NOT_IN_CHAT(324, "the message does not belong to this chat", HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED(325, "Token expired", HttpStatus.BAD_REQUEST),
    NICKNAME_IS_USED(326, "Nick name is used", HttpStatus.BAD_REQUEST),
    SONG_MISMATCH(327, "No user permissions for this song", HttpStatus.FORBIDDEN),
    PLAYLIST_NOT_FOUND(328, "PlayList not found", HttpStatus.NOT_FOUND),
    PLAYLIST_MISMATCH(329, "No user permissions for this playList", HttpStatus.FORBIDDEN),
    SONG_EXISTS(330, "The song is in this playlist", HttpStatus.BAD_REQUEST),
    SONG_NOT_EXISTS(331, "The song is not in this playlist", HttpStatus.BAD_REQUEST),
    STORY_NOT_FOUND(332, "Story not found", HttpStatus.NOT_FOUND),
    STORY_MISMATCH(333, "No user permissions for this story", HttpStatus.FORBIDDEN),
    NOTIFICATION_NOT_FOUND(334, "Notification not found", HttpStatus.NOT_FOUND),
    NOTIFICATION_MISMATCH(335, "No user permissions for this notification", HttpStatus.FORBIDDEN),;
    private final int code;
    private final String description;
    private final HttpStatus httpStatus;
}