import {BASE_API_URL} from "../../config/api.js";
import {
    CREATE_ALBUM_ERROR,
    CREATE_ALBUM_REQUEST,
    GET_ALL_ALBUMS_ERROR,
    GET_ALL_ALBUMS_REQUEST
} from "./ActionType.js";

export const createAlbum = (formData) => async (dispatch) => {

    try {
        const response = await fetch(`${BASE_API_URL}/api/albums/create`,  {
            method: 'POST',
            headers : {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            body: formData
        })

        const album = await response.json()
        console.log("created Post", album)
        dispatch({type: CREATE_ALBUM_REQUEST, payload: album})
    }catch(err) {
        console.log("catch error " + err)
        dispatch({ type: CREATE_ALBUM_ERROR, payload: err.message });
    }
}

export const getAllAlbums = () => async (dispatch) => {
    try {
        const response = await fetch(`${BASE_API_URL}/api/albums/getAll`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
        });

        const albums = await response.json();
        console.log("getAllArtists", albums);
        dispatch({ type: GET_ALL_ALBUMS_REQUEST, payload: albums });
    } catch (error) {
        console.log('catch error', error);
        dispatch({ type: GET_ALL_ALBUMS_ERROR, payload: error.message });
    }
};
