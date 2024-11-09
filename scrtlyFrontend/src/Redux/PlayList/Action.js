import {dispatchAction} from "../../config/api.js";
import {
    CREATE_PLAYLIST_ERROR,
    CREATE_PLAYLIST_REQUEST, DELETE_PLAYLIST_ERROR,
    DELETE_PLAYLIST_REQUEST,
    DELETE_SONG_FROM_PLAYLIST_ERROR,
    DELETE_SONG_FROM_PLAYLIST_REQUEST,
    GET_PLAYLIST_ERROR,
    GET_PLAYLIST_REQUEST,
    GET_PLAYLIST_TRACK_ERROR,
    GET_PLAYLIST_TRACKS_REQUEST,
    GET_USER_PLAYLISTS_ERROR,
    GET_USER_PLAYLISTS_REQUEST,
    UPLOAD_SONG_TO_PLAYLIST_ERROR,
    UPLOAD_SONG_TO_PLAYLIST_REQUEST
} from "./ActionType.js";

export const createPlayList = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, CREATE_PLAYLIST_REQUEST, CREATE_PLAYLIST_ERROR, '/api/playLists/create', {
        method: 'POST',
        body: formData
    });
}

export const getUserPlayLists = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_USER_PLAYLISTS_REQUEST, GET_USER_PLAYLISTS_ERROR, '/api/playLists/user', {
        method: 'GET',
    });
};

export const getPlayList = (playListId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_PLAYLIST_REQUEST, GET_PLAYLIST_ERROR, `/api/playLists/${playListId}`, {
        method: 'GET',
    });
}

export const getPlayListTracks = (playListId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_PLAYLIST_TRACKS_REQUEST, GET_PLAYLIST_TRACK_ERROR, `/api/playLists/${playListId}/tracks`, {
        method: 'GET',
    });
}

export const addSongToPlayList = (data) => async (dispatch) => {
    await dispatchAction(dispatch, UPLOAD_SONG_TO_PLAYLIST_REQUEST, UPLOAD_SONG_TO_PLAYLIST_ERROR, `/api/playLists/${data.playListId}/addSong/${data.songId}`, {
        method: 'PUT',
    });
}

export const deleteSongFromPlayList = (data) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_SONG_FROM_PLAYLIST_REQUEST, DELETE_SONG_FROM_PLAYLIST_ERROR, `/api/playLists/${data.playListId}/deleteSong/${data.songId}`, {
        method: 'DELETE',
    });
}

export const deletePlayList = (playListId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_PLAYLIST_REQUEST, DELETE_PLAYLIST_ERROR, `/api/playLists/delete/${playListId}`, {
        method: 'DELETE',
    });
};