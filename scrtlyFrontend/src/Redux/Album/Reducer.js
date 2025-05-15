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
import {
    DELETE_SONG_FAILURE,
    DELETE_SONG_REQUEST, DELETE_SONG_SUCCESS,
    LIKE_SONG_ERROR,
    LIKE_SONG_REQUEST,
    LIKE_SONG_SUCCESS
} from "../Song/ActionType.js";

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
    likedSong: null,
    deletedSong: null
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
        case LIKE_SONG_REQUEST:
        case DELETE_SONG_REQUEST:
            return { ...state, loading: true, error: null };

        case CREATE_ALBUM_SUCCESS:
            return {
                ...state,
                loading: false,
                createAlbum: payload,
                albums: {
                    ...state.albums,
                    content: [payload, ...state.albums.content],
                    totalElements: state.albums.totalElements + 1
                }
            };
        case GET_ALL_ALBUMS_SUCCESS:
            return { ...state, loading: false, albums: payload };
        case GET_ARTIST_ALBUMS_SUCCESS:
            return { ...state, loading: false, albums2: payload };
        case FIND_ALBUM_SUCCESS:
            return { ...state, loading: false, findAlbum: payload };
        case GET_ALBUM_TRACKS_SUCCESS:
            return { ...state, loading: false, songs: payload };
        case UPLOAD_SONG_SUCCESS:
            return {
                ...state,
                loading: false,
                uploadSong: payload,
                songs: [payload, ...state.songs]
            };
        case DELETE_ALBUM_SUCCESS:
            return {
                ...state,
                loading: false,
                deleteAlbum: payload,
                albums: {
                    ...state.albums,
                    content: state.albums.content.filter(a => a.id !== payload),
                    totalElements: state.albums.totalElements - 1
                }
            };
        case LIKE_SONG_SUCCESS:
            return {
                ...state,
                loading: false,
                likeSong: payload,
                songs: state.songs.map(song =>
                    song.id === payload.song.id
                        ? { ...song, favorite: payload.song.favorite }
                        : song
                )
            };
        case DELETE_SONG_SUCCESS:
            return {
                ...state,
                loading: false,
                deletedSong: payload,
                songs: state.songs.filter(song => song.id !== payload)
            };

        case CREATE_ALBUM_ERROR:
        case GET_ALL_ALBUMS_ERROR:
        case GET_ARTIST_ALBUMS_ERROR:
        case FIND_ALBUM_ERROR:
        case GET_ALBUM_TRACKS_ERROR:
        case UPLOAD_SONG_ERROR:
        case DELETE_ALBUM_ERROR:
        case LIKE_SONG_ERROR:
        case DELETE_SONG_FAILURE:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
}