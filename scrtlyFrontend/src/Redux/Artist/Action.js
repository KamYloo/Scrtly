import {dispatchAction} from "../../config/api.js";
import {
    FIND_ARTIST_BY_ID_ERROR,
    FIND_ARTIST_BY_ID_REQUEST,
    GET_ALL_ARTISTS_ERROR,
    GET_ALL_ARTISTS_REQUEST, GET_ARTIST_TRACKS_ERROR, GET_ARTIST_TRACKS_REQUEST
} from "./ActionType.js";

export const findArtistById = (artistId) => async (dispatch) => {
    await dispatchAction(dispatch, FIND_ARTIST_BY_ID_REQUEST, FIND_ARTIST_BY_ID_ERROR, `/api/artists/${artistId}`, {
        method: 'GET',
    });
}

export const getAllArtists = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_ALL_ARTISTS_REQUEST, GET_ALL_ARTISTS_ERROR, '/api/artists/', {
        method: 'GET',
    });
};

export const updateArtist = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, FIND_ARTIST_BY_ID_REQUEST, FIND_ARTIST_BY_ID_ERROR, '/api/artists/update', {
        method: 'PUT',
        body: formData
    });
};

export const getArtistTracks = (artistId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_ARTIST_TRACKS_REQUEST, GET_ARTIST_TRACKS_ERROR, `/api/artists/${artistId}/tracks`, {
        method: 'GET',
    });
}
