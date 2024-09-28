import {BASE_API_URL} from "../../config/api.js";
import {DELETE_SONG_FAILURE, DELETE_SONG_REQUEST, SEARCH_SONG_ERROR, SEARCH_SONG_REQUEST} from "./ActionType.js";

export const deleteSong = (songId) => async (dispatch) => {
    try {
        const response = await fetch(`${BASE_API_URL}/api/songs/delete/${songId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
        });

        const res = await response.json();
        console.log("Deleted Song", res)
        dispatch({ type: DELETE_SONG_REQUEST, payload: res });
    } catch (error) {
        console.log('catch error', error);
        dispatch({ type: DELETE_SONG_FAILURE, payload: error.message });
    }
};

export const searchSong = (data) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/songs/search?title=${data.keyword}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
        })

        const resData = await res.json()
        console.log("searchSong ", resData)
        dispatch({ type: SEARCH_SONG_REQUEST, payload: resData });
    } catch (error) {
        console.log("catch error ", error)
        dispatch({ type: SEARCH_SONG_ERROR, payload: error.message });

    }
}