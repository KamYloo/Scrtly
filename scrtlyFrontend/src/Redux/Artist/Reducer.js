import {
    FIND_ARTIST_BY_ID_ERROR,
    FIND_ARTIST_BY_ID_REQUEST, FIND_ARTIST_BY_ID_SUCCESS, GET_ALL_ARTISTS_ERROR,
    GET_ALL_ARTISTS_REQUEST, GET_ALL_ARTISTS_SUCCESS, GET_ARTIST_TRACKS_ERROR,
    GET_ARTIST_TRACKS_REQUEST, GET_ARTIST_TRACKS_SUCCESS
} from "./ActionType.js";
import {FOLLOW_USER_ERROR, FOLLOW_USER_REQUEST, FOLLOW_USER_SUCCESS} from "../AuthService/ActionType.js";
import {
    DELETE_SONG_FAILURE,
    DELETE_SONG_REQUEST,
    DELETE_SONG_SUCCESS,
    LIKE_SONG_ERROR,
    LIKE_SONG_REQUEST,
    LIKE_SONG_SUCCESS
} from "../Song/ActionType.js";

const initialValue= {
    loading: false,
    error: null,
    findArtist:null,
    artists: {
        content: [],
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
    },
    updateArtist: null,
    songs: {
        content: [],
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
    },
    follow:null,
    likedSong: null,
    deletedSong: null
}

export const artistReducer=(state=initialValue, {type,payload})=>{
    switch (type) {
        case FIND_ARTIST_BY_ID_REQUEST:
            return { ...state, loading: true, error: null };
        case FIND_ARTIST_BY_ID_SUCCESS:
            return { ...state, loading: false, findArtist: payload };
        case FIND_ARTIST_BY_ID_ERROR:
            return { ...state, loading: false, error: payload };

        case GET_ALL_ARTISTS_REQUEST:
            return { ...state, loading: true, error: null };
        case GET_ALL_ARTISTS_SUCCESS:
            return { ...state, loading: false, artists: payload };
        case GET_ALL_ARTISTS_ERROR:
            return { ...state, loading: false, error: payload };

        case GET_ARTIST_TRACKS_REQUEST:
            return { ...state, loading: true, error: null };
        case GET_ARTIST_TRACKS_SUCCESS:
            return { ...state, loading: false, songs: payload };
        case GET_ARTIST_TRACKS_ERROR:
            return { ...state, loading: false, error: payload };

        case FOLLOW_USER_REQUEST:
            return { ...state, loading: true, error: null };
        case FOLLOW_USER_SUCCESS:
            return { ...state, loading: false, follow: payload };
        case FOLLOW_USER_ERROR:
            return { ...state, loading: false, error: payload };

        case LIKE_SONG_REQUEST:
            return { ...state, loading: true, error: null };
        case LIKE_SONG_SUCCESS:
            return {
                ...state,
                loading: false,
                likeSong: payload,
                songs: {
                    ...state.songs,
                    content: state.songs.content.map(song =>
                        song.id === payload.song.id
                            ? { ...song, favorite: payload.song.favorite }
                            : song
                        )
                }
            };
        case LIKE_SONG_ERROR:
            return { ...state, loading: false, error: payload };

        case DELETE_SONG_REQUEST:
            return { ...state, loading: true, error: null };
        case DELETE_SONG_SUCCESS:
            return {
                ...state,
                loading: false,
                deletedSong: payload,
                songs: {
                    ...state.songs,
                    content: state.songs.content.filter(song => song.id !== payload)
                }
            };
        case DELETE_SONG_FAILURE:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
}