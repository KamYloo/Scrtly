import {dispatchAction} from "../../config/api.js";
import {
    DELETE_SONG_FAILURE,
    DELETE_SONG_REQUEST, DELETE_SONG_SUCCESS, LIKE_SONG_ERROR,
    LIKE_SONG_REQUEST, LIKE_SONG_SUCCESS,
    SEARCH_SONG_ERROR,
    SEARCH_SONG_REQUEST, SEARCH_SONG_SUCCESS
} from "./ActionType.js";


export const deleteSong = (songId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_SONG_REQUEST, DELETE_SONG_SUCCESS, DELETE_SONG_FAILURE, `/api/songs/delete/${songId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
};

export const searchSong = (data) => async (dispatch) => {
    await dispatchAction(dispatch, SEARCH_SONG_REQUEST, SEARCH_SONG_SUCCESS, SEARCH_SONG_ERROR, `/api/songs/search?title=${data.keyword}`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const likeSong = (songId) => async (dispatch) => {
    await dispatchAction(dispatch, LIKE_SONG_REQUEST, LIKE_SONG_SUCCESS, LIKE_SONG_ERROR, `/api/song/${songId}/like`, {
        method: 'PUT',
        body: JSON.stringify(songId),
        credentials: 'include',
    });
}