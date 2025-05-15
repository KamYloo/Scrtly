import {
    CREATE_PLAYLIST_ERROR,
    CREATE_PLAYLIST_REQUEST,
    CREATE_PLAYLIST_SUCCESS,
    DELETE_PLAYLIST_ERROR,
    DELETE_PLAYLIST_REQUEST,
    DELETE_PLAYLIST_SUCCESS,
    DELETE_SONG_FROM_PLAYLIST_ERROR,
    DELETE_SONG_FROM_PLAYLIST_REQUEST,
    DELETE_SONG_FROM_PLAYLIST_SUCCESS,
    GET_PLAYLIST_ERROR,
    GET_PLAYLIST_REQUEST,
    GET_PLAYLIST_SUCCESS,
    GET_PLAYLIST_TRACKS_ERROR,
    GET_PLAYLIST_TRACKS_REQUEST,
    GET_PLAYLIST_TRACKS_SUCCESS,
    GET_USER_PLAYLISTS_ERROR,
    GET_USER_PLAYLISTS_REQUEST,
    GET_USER_PLAYLISTS_SUCCESS,
    UPDATE_PLAYLIST_ERROR,
    UPDATE_PLAYLIST_REQUEST,
    UPDATE_PLAYLIST_SUCCESS,
    UPLOAD_SONG_TO_PLAYLIST_ERROR,
    UPLOAD_SONG_TO_PLAYLIST_REQUEST,
    UPLOAD_SONG_TO_PLAYLIST_SUCCESS,
} from "./ActionType.js";
import {
    LIKE_SONG_ERROR,
    LIKE_SONG_REQUEST,
    LIKE_SONG_SUCCESS
} from "../Song/ActionType.js";

const initialValue = {
    loading: false,
    error: null,
    findPlayList: null,
    playLists: {
        content: [],
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
    },
    createPlayList: null,
    deletePlayList: null,
    songs: {
        content: [],
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
    },
    uploadSong: null,
    deletedSong: null,
    likedSong: null,
};

export const playListReducer = (state = initialValue, { type, payload }) => {
    switch (type) {
        case CREATE_PLAYLIST_REQUEST:
        case GET_USER_PLAYLISTS_REQUEST:
        case GET_PLAYLIST_REQUEST:
        case GET_PLAYLIST_TRACKS_REQUEST:
        case UPLOAD_SONG_TO_PLAYLIST_REQUEST:
        case DELETE_SONG_FROM_PLAYLIST_REQUEST:
        case UPDATE_PLAYLIST_REQUEST:
        case DELETE_PLAYLIST_REQUEST:
        case LIKE_SONG_REQUEST:
            return { ...state, loading: true, error: null };

        case CREATE_PLAYLIST_SUCCESS:
            return {
                ...state,
                loading: false,
                createPlayList: payload,
                playLists: {
                    ...state.playLists,
                    content: [payload, ...state.playLists.content],
                    totalElements: state.playLists.totalElements + 1,
                }
            };

        case GET_USER_PLAYLISTS_SUCCESS:
            return { ...state, loading: false, playLists: payload };

        case GET_PLAYLIST_SUCCESS:
            return { ...state, loading: false, findPlayList: payload };

        case GET_PLAYLIST_TRACKS_SUCCESS:
            return {
                ...state,
                loading: false,
                songs: payload.content ? payload : { content: payload, pageNumber: 0, pageSize: payload.length, totalElements: payload.length, totalPages: 1 }
            };

        case UPLOAD_SONG_TO_PLAYLIST_SUCCESS:
            return { ...state, loading: false, uploadSong: payload };

        case DELETE_SONG_FROM_PLAYLIST_SUCCESS:
            return {
                ...state,
                loading: false,
                deletedSong: payload,
                songs: {
                    ...state.songs,
                    content: state.songs.content.filter(s => s.id !== payload),
                    totalElements: state.songs.totalElements - 1
                }
            };

        case UPDATE_PLAYLIST_SUCCESS:
            return {
                ...state,
                loading: false,
                findPlayList: payload,
                playLists: {
                    ...state.playLists,
                    content: state.playLists.content.map(pl => pl.id === payload.id ? payload : pl)
                }
            };

        case DELETE_PLAYLIST_SUCCESS:
            return {
                ...state,
                loading: false,
                deletePlayList: payload,
                playLists: {
                    ...state.playLists,
                    content: state.playLists.content.filter(pl => pl.id !== payload),
                    totalElements: state.playLists.totalElements - 1
                }
            };

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

        case CREATE_PLAYLIST_ERROR:
        case GET_USER_PLAYLISTS_ERROR:
        case GET_PLAYLIST_ERROR:
        case GET_PLAYLIST_TRACKS_ERROR:
        case UPLOAD_SONG_TO_PLAYLIST_ERROR:
        case DELETE_SONG_FROM_PLAYLIST_ERROR:
        case UPDATE_PLAYLIST_ERROR:
        case DELETE_PLAYLIST_ERROR:
        case LIKE_SONG_ERROR:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
};
