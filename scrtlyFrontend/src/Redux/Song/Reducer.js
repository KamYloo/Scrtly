import {
    DELETE_SONG_FAILURE,
    DELETE_SONG_REQUEST,
    DELETE_SONG_SUCCESS, LIKE_SONG_ERROR,
    LIKE_SONG_REQUEST, LIKE_SONG_SUCCESS, RECORD_PLAY_ERROR, RECORD_PLAY_REQUEST, RECORD_PLAY_SUCCESS,
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
    isPlaying: true,
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

        case RECORD_PLAY_REQUEST:
            return { ...state, error: null };
        case RECORD_PLAY_SUCCESS:
            return { ...state, isPlaying: payload };
        case RECORD_PLAY_ERROR:
            return { ...state, error: payload };

        default:
            return state;
    }
}