import {
    VERIFY_ARTIST_REQUEST,
    VERIFY_ARTIST_SUCCESS,
    VERIFY_ARTIST_ERROR
} from "./ActionType"
import {dispatchAction} from "../api.js";


export const verifyArtistAction = (artistName) => async (dispatch) => {
    await dispatchAction(
        dispatch,
        VERIFY_ARTIST_REQUEST,
        VERIFY_ARTIST_SUCCESS,
        VERIFY_ARTIST_ERROR,
        `/user/verify-request`,
        {
            method: 'POST',
            body: JSON.stringify({ requestedArtistName: artistName }),
            credentials: 'include'
        }
    );
}