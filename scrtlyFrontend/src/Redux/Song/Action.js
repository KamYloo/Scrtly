import {BASE_API_URL} from "../../config/api.js";
import {DELETE_SONG_FAILURE, DELETE_SONG_REQUEST} from "./ActionType.js";

export const deleteSong = (songId) => async (dispatch) => {
    try {
        const response = await fetch(`${BASE_API_URL}/api/songs/delete/${songId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
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