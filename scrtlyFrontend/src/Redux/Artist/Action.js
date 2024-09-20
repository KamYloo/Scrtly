import {BASE_API_URL} from "../../config/api.js";
import {
    FIND_ARTIST_BY_ID_ERROR,
    FIND_ARTIST_BY_ID_REQUEST,
    GET_ALL_ARTISTS_ERROR,
    GET_ALL_ARTISTS_REQUEST
} from "./ActionType.js";

export const findArtistById = (artistId) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/artists/${artistId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
        })

        const resData = await res.json()
        console.log("Find artist ", resData)
        dispatch({ type: FIND_ARTIST_BY_ID_REQUEST, payload: resData })
    } catch (error) {
        console.log("catch error ", error)
        dispatch({ type: FIND_ARTIST_BY_ID_ERROR, payload: error.message });
    }
}

export const getAllArtists = () => async (dispatch) => {
    try {
        const response = await fetch(`${BASE_API_URL}/api/artists/`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
        });

        const artists = await response.json();
        console.log("getAllArtists", artists);
        dispatch({ type: GET_ALL_ARTISTS_REQUEST, payload: artists });
    } catch (error) {
        console.log('catch error', error);
        dispatch({ type: GET_ALL_ARTISTS_ERROR, payload: error.message });
    }
};

export const updateArtist = (formData) => async (dispatch) => {
    try {
        const response = await fetch(`${BASE_API_URL}/api/artists/update`, {
            method: 'PUT',
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
            body: formData
        });


        const artist = await response.json();
        console.log("updateArtist", artist);
        dispatch({ type: FIND_ARTIST_BY_ID_REQUEST, payload: artist });
    } catch (error) {
        console.log('catch error', error);
        dispatch({ type: FIND_ARTIST_BY_ID_ERROR, payload: error.message });
    }
};