import {BASE_API_URL} from "../../config/api.js";
import {
    CREATE_PLAYLIST_ERROR,
    CREATE_PLAYLIST_REQUEST,
    GET_PLAYLIST_ERROR,
    GET_PLAYLIST_REQUEST,
    GET_PLAYLIST_TRACK_ERROR,
    GET_PLAYLIST_TRACKS_REQUEST,
    GET_USER_PLAYLISTS_ERROR,
    GET_USER_PLAYLISTS_REQUEST
} from "./ActionType.js";
import {
    FIND_ALBUM_ERROR,
    FIND_ALBUM_REQUEST, GET_ALBUM_TRACKS_ERROR, GET_ALBUM_TRACKS_REQUEST,
    GET_ARTIST_ALBUMS_ERROR,
    GET_ARTIST_ALBUMS_REQUEST
} from "../Album/ActionType.js";

export const createPlayList = (formData) => async (dispatch) => {

    try {
        const response = await fetch(`${BASE_API_URL}/api/playLists/create`,  {
            method: 'POST',
            headers : {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            body: formData
        })

        const playList = await response.json()
        console.log("created PlayList", playList)
        dispatch({type: CREATE_PLAYLIST_REQUEST, payload: playList})
    }catch(err) {
        console.log("catch error " + err)
        dispatch({ type: CREATE_PLAYLIST_ERROR, payload: err.message });
    }
}

export const getUserPlayLists = () => async (dispatch) => {
    try {
        const response = await fetch(`${BASE_API_URL}/api/playLists/user`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
        });
        const playLists = await response.json();
        console.log("getUserPlayLists", playLists);
        dispatch({ type: GET_USER_PLAYLISTS_REQUEST, payload: playLists });
    } catch (error) {
        console.log('catch error', error);
        dispatch({ type: GET_USER_PLAYLISTS_ERROR, payload: error.message });
    }
};

export const getPlayList = (playListId) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/playLists/${playListId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
        })

        const resData = await res.json()
        console.log("Find PlayList ", resData)
        dispatch({ type: GET_PLAYLIST_REQUEST, payload: resData })
    } catch (error) {
        console.log("catch error ", error)
        dispatch({ type: GET_PLAYLIST_ERROR, payload: error.message });
    }
}

export const getPlayListTracks = (playListId) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/playLists/${playListId}/tracks`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
        })

        const resData = await res.json()
        console.log("Find tracks ", resData)
        dispatch({ type: GET_PLAYLIST_TRACKS_REQUEST, payload: resData })
    } catch (error) {
        console.log("catch error ", error)
        dispatch({ type: GET_PLAYLIST_TRACK_ERROR, payload: error.message });
    }
}