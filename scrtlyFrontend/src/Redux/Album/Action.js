import {dispatchAction} from "../api.js";
import {
    CREATE_ALBUM_ERROR,
    CREATE_ALBUM_REQUEST, CREATE_ALBUM_SUCCESS, DELETE_ALBUM_ERROR,
    DELETE_ALBUM_REQUEST, DELETE_ALBUM_SUCCESS,
    FIND_ALBUM_ERROR,
    FIND_ALBUM_REQUEST, FIND_ALBUM_SUCCESS,
    GET_ALBUM_TRACKS_ERROR,
    GET_ALBUM_TRACKS_REQUEST, GET_ALBUM_TRACKS_SUCCESS,
    GET_ALL_ALBUMS_ERROR,
    GET_ALL_ALBUMS_REQUEST, GET_ALL_ALBUMS_SUCCESS,
    GET_ARTIST_ALBUMS_ERROR,
    GET_ARTIST_ALBUMS_REQUEST, GET_ARTIST_ALBUMS_SUCCESS,
    UPLOAD_SONG_ERROR,
    UPLOAD_SONG_REQUEST, UPLOAD_SONG_SUCCESS
} from "./ActionType.js";

export const createAlbum = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, CREATE_ALBUM_REQUEST, CREATE_ALBUM_SUCCESS, CREATE_ALBUM_ERROR, '/album/create', {
        method: 'POST',
        body: formData,
        credentials: 'include' }
    );
};

export const getAllAlbums = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_ALL_ALBUMS_REQUEST, GET_ALL_ALBUMS_SUCCESS,  GET_ALL_ALBUMS_ERROR, '/album/all', {
        method: 'GET',
        credentials: 'include',
    });
};

export const getArtistAlbums = (artistId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_ARTIST_ALBUMS_REQUEST, GET_ARTIST_ALBUMS_SUCCESS, GET_ARTIST_ALBUMS_ERROR, `/album/artist/${artistId}`, {
        method: 'GET',
        credentials: 'include',
    });
};

export const getAlbum = (albumId) => async (dispatch) => {
    await dispatchAction(dispatch, FIND_ALBUM_REQUEST, FIND_ALBUM_SUCCESS, FIND_ALBUM_ERROR, `/album/${albumId}`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const getAlbumTracks = (albumId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_ALBUM_TRACKS_REQUEST, GET_ALBUM_TRACKS_SUCCESS, GET_ALBUM_TRACKS_ERROR, `/album/${albumId}/tracks`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const uploadSong = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, UPLOAD_SONG_REQUEST, UPLOAD_SONG_SUCCESS, UPLOAD_SONG_ERROR, '/song/upload', {
        method: 'POST',
        body: formData,
        credentials: 'include',
    });
}

export const deleteAlbum = (albumId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_ALBUM_REQUEST, DELETE_ALBUM_SUCCESS, DELETE_ALBUM_ERROR, `/album/delete/${albumId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
};
