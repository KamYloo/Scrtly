import {
    DELETE_SONG_FAILURE,
    DELETE_SONG_REQUEST,
    DELETE_SONG_SUCCESS, LIKE_SONG_ERROR,
    LIKE_SONG_REQUEST, LIKE_SONG_SUCCESS,
    SEARCH_SONG_ERROR,
    SEARCH_SONG_REQUEST,
    SEARCH_SONG_SUCCESS
} from "./ActionType.js";

const initialValue= {
    loading: false,
    error: null,
    deletedSong:null,
    searchResults: null,
    likedSong: null,
}

export const songReducer=(state=initialValue, {type,payload})=>{
    switch (type) {
        case DELETE_SONG_REQUEST:
            return { ...state, loading: true, error: null };
        case DELETE_SONG_SUCCESS:
            return { ...state, loading: false, deletedSong: payload };
        case DELETE_SONG_FAILURE:
            return { ...state, loading: false, error: payload };

        case SEARCH_SONG_REQUEST:
            return { ...state, loading: true, error: null };
        case SEARCH_SONG_SUCCESS:
            return { ...state, loading: false, searchResults: payload };
        case SEARCH_SONG_ERROR:
            return { ...state, loading: false, error: payload };

        case LIKE_SONG_REQUEST:
            return { ...state, loading: true, error: null };
        case LIKE_SONG_SUCCESS:
            return { ...state, loading: false, likedSong: payload };
        case LIKE_SONG_ERROR:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
}