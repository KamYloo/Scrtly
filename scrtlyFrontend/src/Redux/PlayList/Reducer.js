import {
    CREATE_PLAYLIST_ERROR,
    CREATE_PLAYLIST_REQUEST,
    CREATE_PLAYLIST_SUCCESS, DELETE_PLAYLIST_ERROR,
    DELETE_PLAYLIST_REQUEST, DELETE_PLAYLIST_SUCCESS, DELETE_SONG_FROM_PLAYLIST_ERROR,
    DELETE_SONG_FROM_PLAYLIST_REQUEST, DELETE_SONG_FROM_PLAYLIST_SUCCESS, GET_PLAYLIST_ERROR,
    GET_PLAYLIST_REQUEST,
    GET_PLAYLIST_SUCCESS, GET_PLAYLIST_TRACK_ERROR, GET_PLAYLIST_TRACK_SUCCESS,
    GET_PLAYLIST_TRACKS_REQUEST,
    GET_USER_PLAYLIST_SUCCESS,
    GET_USER_PLAYLISTS_ERROR,
    GET_USER_PLAYLISTS_REQUEST, UPDATE_PLAYLIST_ERROR,
    UPDATE_PLAYLIST_REQUEST, UPDATE_PLAYLIST_SUCCESS, UPLOAD_SONG_TO_PLAYLIST_ERROR,
    UPLOAD_SONG_TO_PLAYLIST_REQUEST, UPLOAD_SONG_TO_PLAYLIST_SUCCESS,
} from "./ActionType.js";


const initialValue= {
    loading: false,
    error: null,
    findPlayList:null,
    playLists: {
        content: [],
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
    },
    createPlayList : null,
    deletePlayList : null,
    songs: {
        content: [],
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
    },
    uploadSong: null,
    deletedSong: null,
}

export const playListReducer=(state=initialValue, {type,payload})=>{
    switch (type) {
        case CREATE_PLAYLIST_REQUEST:
            return { ...state, loading: true, error: null };
        case CREATE_PLAYLIST_SUCCESS:
            return { ...state, loading: false, createPlayList: payload };
        case CREATE_PLAYLIST_ERROR:
            return { ...state, loading: false, error: payload };

        case GET_USER_PLAYLISTS_REQUEST:
            return { ...state, loading: true, error: null };
        case GET_USER_PLAYLIST_SUCCESS:
            return { ...state, loading: false, playLists: payload };
        case GET_USER_PLAYLISTS_ERROR:
            return { ...state, loading: false, error: payload };

        case GET_PLAYLIST_REQUEST:
            return { ...state, loading: true, error: null };
        case GET_PLAYLIST_SUCCESS:
            return { ...state, loading: false, findPlayList: payload };
        case GET_PLAYLIST_ERROR:
            return { ...state, loading: false, error: payload };

        case GET_PLAYLIST_TRACKS_REQUEST:
            return { ...state, loading: true, error: null };
        case GET_PLAYLIST_TRACK_SUCCESS:
            return { ...state, loading: false, songs: payload };
        case GET_PLAYLIST_TRACK_ERROR:
            return { ...state, loading: false, error: payload };

        case UPLOAD_SONG_TO_PLAYLIST_REQUEST:
            return { ...state, loading: true, error: null };
        case UPLOAD_SONG_TO_PLAYLIST_SUCCESS:
            return { ...state, loading: false, uploadSong: payload };
        case UPLOAD_SONG_TO_PLAYLIST_ERROR:
            return { ...state, loading: false, error: payload };

        case DELETE_SONG_FROM_PLAYLIST_REQUEST:
            return { ...state, loading: true, error: null };
        case DELETE_SONG_FROM_PLAYLIST_SUCCESS:
            return { ...state, loading: false, deletedSong: payload };
        case DELETE_SONG_FROM_PLAYLIST_ERROR:
            return { ...state, loading: false, error: payload };

        case UPDATE_PLAYLIST_REQUEST:
            return { ...state, loading: true, error: null };
        case UPDATE_PLAYLIST_SUCCESS:
            return { ...state, loading: false, findPlayList: payload };
        case UPDATE_PLAYLIST_ERROR:
            return { ...state, loading: false, error: payload };

        case DELETE_PLAYLIST_REQUEST:
            return { ...state, loading: true, error: null };
        case DELETE_PLAYLIST_SUCCESS:
            return { ...state, loading: false, deletePlayList: payload };
        case DELETE_PLAYLIST_ERROR:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
}