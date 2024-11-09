import {dispatchAction} from "../../config/api.js";
import {
    DELETE_SONG_FAILURE,
    DELETE_SONG_REQUEST, LIKE_SONG_ERROR,
    LIKE_SONG_REQUEST,
    SEARCH_SONG_ERROR,
    SEARCH_SONG_REQUEST
} from "./ActionType.js";


export const deleteSong = (songId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_SONG_REQUEST, DELETE_SONG_FAILURE, `/api/songs/delete/${songId}`, {
        method: 'DELETE',
    });
};

export const searchSong = (data) => async (dispatch) => {
    await dispatchAction(dispatch, SEARCH_SONG_REQUEST, SEARCH_SONG_ERROR, `/api/songs/search?title=${data.keyword}`, {
        method: 'GET',
    });
}

export const likeSong = (songId) => async (dispatch) => {
    await dispatchAction(dispatch, LIKE_SONG_REQUEST, LIKE_SONG_ERROR, `/api/song/${songId}/like`, {
        method: 'POST',
        body: JSON.stringify(songId)
    });
}