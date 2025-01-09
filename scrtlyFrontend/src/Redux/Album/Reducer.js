import {
    CREATE_ALBUM_REQUEST, DELETE_ALBUM_REQUEST, FIND_ALBUM_REQUEST, GET_ALBUM_TRACKS_REQUEST,
    GET_ALL_ALBUMS_REQUEST, GET_ARTIST_ALBUMS_REQUEST, UPLOAD_SONG_REQUEST,
} from "./ActionType.js";

const initialValue= {
    findAlbum:null,
    albums: {
        content: [],
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
    },
    albums2: [],
    createAlbum : null,
    deleteAlbum : null,
    songs: [],
    uploadSong: null,
}

export const albumReducer=(store=initialValue, {type,payload})=>{
    if (type === CREATE_ALBUM_REQUEST) {
        return {...store, createAlbum: payload}
    }
    else if (type === GET_ALL_ALBUMS_REQUEST) {
        return {...store, albums: payload}
    }
    else if(type === GET_ARTIST_ALBUMS_REQUEST) {
        return {...store, albums2: payload}
    }
    else if(type === FIND_ALBUM_REQUEST) {
        return {...store, findAlbum: payload}
    }
    else if(type === UPLOAD_SONG_REQUEST) {
        return {...store, uploadSong: payload}
    }
    else if(type === GET_ALBUM_TRACKS_REQUEST) {
        return {...store, songs: payload}
    }
    else if (type === DELETE_ALBUM_REQUEST) {
        return {...store, deleteAlbum: payload}
    }
    return store
}