import {BASE_API_URL} from "../../config/api.js";
import {
    CREATE_ALBUM_ERROR,
    CREATE_ALBUM_REQUEST, FIND_ALBUM_ERROR, FIND_ALBUM_REQUEST, GET_ALBUM_TRACKS_ERROR, GET_ALBUM_TRACKS_REQUEST,
    GET_ALL_ALBUMS_ERROR,
    GET_ALL_ALBUMS_REQUEST, UPLOAD_SONG_ERROR, UPLOAD_SONG_REQUEST
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
        console.log("created Album", album)
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
        console.log("getAllAlbums", albums);
        dispatch({ type: GET_ALL_ALBUMS_REQUEST, payload: albums });
    } catch (error) {
        console.log('catch error', error);
        dispatch({ type: GET_ALL_ALBUMS_ERROR, payload: error.message });
    }
};

export const getAlbum = (albumId) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/albums/${albumId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
        })

        const resData = await res.json()
        console.log("Find album ", resData)
        dispatch({ type: FIND_ALBUM_REQUEST, payload: resData })
    } catch (error) {
        console.log("catch error ", error)
        dispatch({ type: FIND_ALBUM_ERROR, payload: error.message });
    }
}

export const getAlbumTracks = (albumId) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/albums/${albumId}/tracks`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
        })

        const resData = await res.json()
        console.log("Find tracks ", resData)
        dispatch({ type: GET_ALBUM_TRACKS_REQUEST, payload: resData })
    } catch (error) {
        console.log("catch error ", error)
        dispatch({ type: GET_ALBUM_TRACKS_ERROR, payload: error.message });
    }
}

export const uploadSong = (formData) => async (dispatch) => {

    try {
        const response = await fetch(`${BASE_API_URL}/api/songs/upload`,  {
            method: 'POST',
            headers : {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            body: formData
        })

        const resData = await response.json()
        console.log("created Song", resData)
        dispatch({type: UPLOAD_SONG_REQUEST, payload: resData})
    }catch(err) {
        console.log("catch error " + err)
        dispatch({ type: UPLOAD_SONG_ERROR, payload: err.message });
    }
}
