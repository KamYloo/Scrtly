import {
    CREATE_ALBUM_REQUEST, FIND_ALBUM_REQUEST, GET_ALBUM_TRACKS_REQUEST,
    GET_ALL_ALBUMS_REQUEST, UPLOAD_SONG_REQUEST,
} from "./ActionType.js";

const initialValue= {
    findAlbum:null,
    albums: [],
    createAlbum : null,
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
    else if(type === FIND_ALBUM_REQUEST) {
        return {...store, findAlbum: payload}
    }
    else if(type === UPLOAD_SONG_REQUEST) {
        return {...store, uploadSong: payload}
    }
    else if(type === GET_ALBUM_TRACKS_REQUEST) {
        return {...store, songs: payload}
    }

    return store
}