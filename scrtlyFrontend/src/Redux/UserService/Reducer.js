import {
    VERIFY_ARTIST_REQUEST,
    VERIFY_ARTIST_SUCCESS,
    VERIFY_ARTIST_ERROR
} from "./ActionType.js";

const initialValue= {
    loading: false,
    error: null,
}

export const userReducer=(state=initialValue, {type,payload})=>{
    switch(type) {
        case VERIFY_ARTIST_REQUEST:
            return { ...state, loading: true, error: null };
        case VERIFY_ARTIST_SUCCESS:
            return { ...state, loading: false };
        case VERIFY_ARTIST_ERROR:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
}