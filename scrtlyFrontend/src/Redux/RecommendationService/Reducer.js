import {
    GET_ARTISTS_RECOMMENDED_REQUEST,
    GET_ALBUMS_RECOMMENDED_REQUEST,
    GET_SONGS_RECOMMENDED_REQUEST,
    GET_ARTISTS_RECOMMENDED_SUCCESS,
    GET_ALBUMS_RECOMMENDED_SUCCESS,
    GET_SONGS_RECOMMENDED_SUCCESS,
    GET_ARTISTS_RECOMMENDED_ERROR, GET_ALBUMS_RECOMMENDED_ERROR, GET_SONGS_RECOMMENDED_ERROR
} from "./ActionType.js";
import {LIKE_SONG_SUCCESS} from "../Song/ActionType.js";

const initialValue= {
    loading: false,
    error: null,
    albums:[],
    artists:[],
    songs:[],
}

export const recommendationReducer=(state=initialValue, {type,payload})=>{
    switch (type) {
        case GET_ARTISTS_RECOMMENDED_REQUEST:
        case GET_ALBUMS_RECOMMENDED_REQUEST:
        case GET_SONGS_RECOMMENDED_REQUEST:
            return { ...state, loading: true, error: null };

        case GET_ARTISTS_RECOMMENDED_SUCCESS:
            return { ...state, loading: false, artists: payload };

        case GET_ALBUMS_RECOMMENDED_SUCCESS:
            return { ...state, loading: false, albums: payload };

        case GET_SONGS_RECOMMENDED_SUCCESS:
            return { ...state, loading: false, songs: payload };

        case LIKE_SONG_SUCCESS:
            return {
                ...state,
                songs: state.songs.map(song =>
                    song.id === payload.song.id
                        ? { ...song, favorite: payload.song.favorite }
                        : song
                )
            };

        case GET_ARTISTS_RECOMMENDED_ERROR:
        case GET_ALBUMS_RECOMMENDED_ERROR:
        case GET_SONGS_RECOMMENDED_ERROR:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
}