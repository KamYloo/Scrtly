import {dispatchAction} from "../../config/api.js";
import {
    CREATE_ALBUM_ERROR,
    CREATE_ALBUM_REQUEST, DELETE_ALBUM_ERROR,
    DELETE_ALBUM_REQUEST,
    FIND_ALBUM_ERROR,
    FIND_ALBUM_REQUEST,
    GET_ALBUM_TRACKS_ERROR,
    GET_ALBUM_TRACKS_REQUEST,
    GET_ALL_ALBUMS_ERROR,
    GET_ALL_ALBUMS_REQUEST,
    GET_ARTIST_ALBUMS_ERROR,
    GET_ARTIST_ALBUMS_REQUEST,
    UPLOAD_SONG_ERROR,
    UPLOAD_SONG_REQUEST
} from "./ActionType.js";

export const createAlbum = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, CREATE_ALBUM_REQUEST, CREATE_ALBUM_ERROR, '/api/albums/create', {
        method: 'POST',
        body: formData
    });
}

export const getAllAlbums = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_ALL_ALBUMS_REQUEST, GET_ALL_ALBUMS_ERROR, '/api/albums/getAll');
};

export const getArtistAlbums = (artistId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_ARTIST_ALBUMS_REQUEST, GET_ARTIST_ALBUMS_ERROR, `/api/albums/artist/${artistId}`);
};

export const getAlbum = (albumId) => async (dispatch) => {
    await dispatchAction(dispatch, FIND_ALBUM_REQUEST, FIND_ALBUM_ERROR, `/api/albums/${albumId}`);
}

export const getAlbumTracks = (albumId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_ALBUM_TRACKS_REQUEST, GET_ALBUM_TRACKS_ERROR, `/api/albums/${albumId}/tracks`);
}

export const uploadSong = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, UPLOAD_SONG_REQUEST, UPLOAD_SONG_ERROR, '/api/songs/upload', {
        method: 'POST',
        body: formData
    });
}

export const deleteAlbum = (albumId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_ALBUM_REQUEST, DELETE_ALBUM_ERROR, `/api/albums/delete/${albumId}`, {method: 'DELETE'});
};
