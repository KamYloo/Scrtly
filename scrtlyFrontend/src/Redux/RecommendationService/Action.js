import {dispatchAction} from "../api.js";
import {
    GET_ALBUMS_RECOMMENDED_ERROR,
    GET_ALBUMS_RECOMMENDED_REQUEST,
    GET_ALBUMS_RECOMMENDED_SUCCESS,
    GET_ARTISTS_RECOMMENDED_ERROR,
    GET_ARTISTS_RECOMMENDED_REQUEST,
    GET_ARTISTS_RECOMMENDED_SUCCESS,
    GET_SONGS_RECOMMENDED_ERROR,
    GET_SONGS_RECOMMENDED_REQUEST,
    GET_SONGS_RECOMMENDED_SUCCESS
} from "./ActionType.js";


export const getTopAlbumsAction = (window = 'day', n = 6) => async (dispatch) => {
    await dispatchAction(
        dispatch,
        GET_ALBUMS_RECOMMENDED_REQUEST,
        GET_ALBUMS_RECOMMENDED_SUCCESS,
        GET_ALBUMS_RECOMMENDED_ERROR,
        `/api/recommendations/top-albums?window=${window}&n=${n}`, {
        method: 'GET',
        credentials: 'include',
    });
};

export const getTopArtistsAction = (window = 'day', n = 8) => async (dispatch) => {
    await dispatchAction(
        dispatch,
        GET_ARTISTS_RECOMMENDED_REQUEST,
        GET_ARTISTS_RECOMMENDED_SUCCESS,
        GET_ARTISTS_RECOMMENDED_ERROR,
        `/api/recommendations/top-artists?window=${window}&n=${n}`, {
        method: 'GET',
        credentials: 'include',
    });
};

export const getTopSongsAction = (window = 'day', n = 6) => async (dispatch) => {
    await dispatchAction(
        dispatch,
        GET_SONGS_RECOMMENDED_REQUEST,
        GET_SONGS_RECOMMENDED_SUCCESS,
        GET_SONGS_RECOMMENDED_ERROR,
        `/api/recommendations/top-songs?window=${window}&n=${n}`, {
        method: 'GET',
        credentials: 'include',
    });
};
