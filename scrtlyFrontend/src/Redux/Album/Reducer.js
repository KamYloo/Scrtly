import {
    CREATE_ALBUM_ERROR,
    CREATE_ALBUM_REQUEST,
    CREATE_ALBUM_SUCCESS, DELETE_ALBUM_ERROR,
    DELETE_ALBUM_REQUEST, DELETE_ALBUM_SUCCESS, FIND_ALBUM_ERROR,
    FIND_ALBUM_REQUEST, FIND_ALBUM_SUCCESS, GET_ALBUM_TRACKS_ERROR,
    GET_ALBUM_TRACKS_REQUEST, GET_ALBUM_TRACKS_SUCCESS, GET_ALL_ALBUMS_ERROR,
    GET_ALL_ALBUMS_REQUEST,
    GET_ALL_ALBUMS_SUCCESS, GET_ARTIST_ALBUMS_ERROR,
    GET_ARTIST_ALBUMS_REQUEST,
    GET_ARTIST_ALBUMS_SUCCESS, UPLOAD_SONG_ERROR,
    UPLOAD_SONG_REQUEST, UPLOAD_SONG_SUCCESS,
} from "./ActionType.js";

const initialValue= {
    loading: false,
    error: null,
    findAlbum: null,
    albums: {
        content: [],
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
    },
    albums2: [],
    createAlbum: null,
    deleteAlbum: null,
    songs: [],
    uploadSong: null,
}

export const albumReducer=(state=initialValue, {type,payload})=>{
    switch (type) {
        case CREATE_ALBUM_REQUEST:
        case GET_ALL_ALBUMS_REQUEST:
        case GET_ARTIST_ALBUMS_REQUEST:
        case FIND_ALBUM_REQUEST:
        case GET_ALBUM_TRACKS_REQUEST:
        case UPLOAD_SONG_REQUEST:
        case DELETE_ALBUM_REQUEST:
            return { ...state, loading: true, error: null };

        case CREATE_ALBUM_SUCCESS:
            return { ...state, loading: false, createAlbum: payload };
        case GET_ALL_ALBUMS_SUCCESS:
            return { ...state, loading: false, albums: payload };
        case GET_ARTIST_ALBUMS_SUCCESS:
            return { ...state, loading: false, albums2: payload };
        case FIND_ALBUM_SUCCESS:
            return { ...state, loading: false, findAlbum: payload };
        case GET_ALBUM_TRACKS_SUCCESS:
            return { ...state, loading: false, songs: payload };
        case UPLOAD_SONG_SUCCESS:
            return { ...state, loading: false, uploadSong: payload };
        case DELETE_ALBUM_SUCCESS:
            return { ...state, loading: false, deleteAlbum: payload };

        case CREATE_ALBUM_ERROR:
        case GET_ALL_ALBUMS_ERROR:
        case GET_ARTIST_ALBUMS_ERROR:
        case FIND_ALBUM_ERROR:
        case GET_ALBUM_TRACKS_ERROR:
        case UPLOAD_SONG_ERROR:
        case DELETE_ALBUM_ERROR:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
}