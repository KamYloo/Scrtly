import {dispatchAction} from "../api.js";
import {
    FIND_ARTIST_BY_ID_ERROR,
    FIND_ARTIST_BY_ID_REQUEST,
    FIND_ARTIST_BY_ID_SUCCESS,
    GET_ALL_ARTISTS_ERROR,
    GET_ALL_ARTISTS_REQUEST,
    GET_ALL_ARTISTS_SUCCESS,
    GET_ARTIST_TRACKS_ERROR,
    GET_ARTIST_TRACKS_REQUEST,
    GET_ARTIST_TRACKS_SUCCESS
} from "./ActionType.js";

export const findArtistById = (artistId) => async (dispatch) => {
    await dispatchAction(dispatch, FIND_ARTIST_BY_ID_REQUEST, FIND_ARTIST_BY_ID_SUCCESS, FIND_ARTIST_BY_ID_ERROR, `/api/artist/${artistId}`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const getAllArtists = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_ALL_ARTISTS_REQUEST, GET_ALL_ARTISTS_SUCCESS, GET_ALL_ARTISTS_ERROR, '/api/artist/all', {
        method: 'GET',
        credentials: 'include',
    });
};

export const updateArtist = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, FIND_ARTIST_BY_ID_REQUEST, FIND_ARTIST_BY_ID_SUCCESS, FIND_ARTIST_BY_ID_ERROR, '/api/artist/update', {
        method: 'PUT',
        body: formData,
        credentials: 'include',
    });
};

export const getArtistTracks = (artistId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_ARTIST_TRACKS_REQUEST, GET_ARTIST_TRACKS_SUCCESS, GET_ARTIST_TRACKS_ERROR, `/api/artist/${artistId}/tracks`, {
        method: 'GET',
        credentials: 'include',
    });
}
