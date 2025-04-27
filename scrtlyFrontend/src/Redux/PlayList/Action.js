import {dispatchAction} from "../../config/api.js";
import {
    CREATE_PLAYLIST_ERROR,
    CREATE_PLAYLIST_REQUEST, CREATE_PLAYLIST_SUCCESS, DELETE_PLAYLIST_ERROR,
    DELETE_PLAYLIST_REQUEST, DELETE_PLAYLIST_SUCCESS,
    DELETE_SONG_FROM_PLAYLIST_ERROR,
    DELETE_SONG_FROM_PLAYLIST_REQUEST, DELETE_SONG_FROM_PLAYLIST_SUCCESS,
    GET_PLAYLIST_ERROR,
    GET_PLAYLIST_REQUEST, GET_PLAYLIST_SUCCESS,
    GET_PLAYLIST_TRACK_ERROR, GET_PLAYLIST_TRACK_SUCCESS,
    GET_PLAYLIST_TRACKS_REQUEST, GET_USER_PLAYLIST_SUCCESS,
    GET_USER_PLAYLISTS_ERROR,
    GET_USER_PLAYLISTS_REQUEST, UPDATE_PLAYLIST_ERROR, UPDATE_PLAYLIST_REQUEST, UPDATE_PLAYLIST_SUCCESS,
    UPLOAD_SONG_TO_PLAYLIST_ERROR,
    UPLOAD_SONG_TO_PLAYLIST_REQUEST, UPLOAD_SONG_TO_PLAYLIST_SUCCESS
} from "./ActionType.js";

export const createPlayList = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, CREATE_PLAYLIST_REQUEST, CREATE_PLAYLIST_SUCCESS, CREATE_PLAYLIST_ERROR, '/playLists/create', {
        method: 'POST',
        body: formData,
        credentials: 'include',
    });
}

export const getUserPlayLists = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_USER_PLAYLISTS_REQUEST, GET_USER_PLAYLIST_SUCCESS, GET_USER_PLAYLISTS_ERROR, '/playLists/user', {
        method: 'GET',
        credentials: 'include',
    });
};

export const getPlayList = (playListId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_PLAYLIST_REQUEST, GET_PLAYLIST_SUCCESS, GET_PLAYLIST_ERROR, `/playLists/${playListId}`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const getPlayListTracks = (playListId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_PLAYLIST_TRACKS_REQUEST, GET_PLAYLIST_TRACK_SUCCESS, GET_PLAYLIST_TRACK_ERROR, `/playLists/${playListId}/tracks`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const addSongToPlayList = (data) => async (dispatch) => {
    await dispatchAction(dispatch, UPLOAD_SONG_TO_PLAYLIST_REQUEST, UPLOAD_SONG_TO_PLAYLIST_SUCCESS, UPLOAD_SONG_TO_PLAYLIST_ERROR, `/playLists/${data.playListId}/addSong/${data.songId}`, {
        method: 'PUT',
        credentials: 'include',
    });
}

export const deleteSongFromPlayList = (data) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_SONG_FROM_PLAYLIST_REQUEST, DELETE_SONG_FROM_PLAYLIST_SUCCESS, DELETE_SONG_FROM_PLAYLIST_ERROR, `/playLists/${data.playListId}/deleteSong/${data.songId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
}

export const updatePlayList = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, UPDATE_PLAYLIST_REQUEST, UPDATE_PLAYLIST_SUCCESS, UPDATE_PLAYLIST_ERROR, `/playLists/update`, {
        method: 'PUT',
        body: formData,
        credentials: 'include',
    });
}

export const deletePlayList = (playListId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_PLAYLIST_REQUEST, DELETE_PLAYLIST_SUCCESS, DELETE_PLAYLIST_ERROR, `/playLists/delete/${playListId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
};