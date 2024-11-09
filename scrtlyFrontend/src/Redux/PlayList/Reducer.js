import {
    CREATE_PLAYLIST_REQUEST,
    DELETE_PLAYLIST_REQUEST, DELETE_SONG_FROM_PLAYLIST_REQUEST,
    GET_PLAYLIST_REQUEST, GET_PLAYLIST_TRACKS_REQUEST,
    GET_USER_PLAYLISTS_REQUEST, UPDATE_PLAYLIST_REQUEST,
    UPLOAD_SONG_TO_PLAYLIST_REQUEST,
} from "./ActionType.js";


const initialValue= {
    findPlayList:null,
    playLists: [],
    createPlayList : null,
    deletePlayList : null,
    songs: [],
    uploadSong: null,
    deletedSong: null,
}

export const playListReducer=(store=initialValue, {type,payload})=>{
    if (type === CREATE_PLAYLIST_REQUEST) {
        return {...store, createPlayList: payload}
    }
    else if(type === GET_USER_PLAYLISTS_REQUEST) {
        return {...store, playLists: payload}
    }
    else if(type === GET_PLAYLIST_REQUEST) {
        return {...store, findPlayList: payload}
    }
    else if(type === UPLOAD_SONG_TO_PLAYLIST_REQUEST) {
        return {...store, uploadSong: payload}
    }
    else if(type === DELETE_SONG_FROM_PLAYLIST_REQUEST) {
        return {...store, deletedSong: payload}
    }
    else if(type === GET_PLAYLIST_TRACKS_REQUEST) {
        return {...store, songs: payload}
    }
    else if (type === UPDATE_PLAYLIST_REQUEST) {
        return {...store, findPlayList: payload}
    }
    else if (type === DELETE_PLAYLIST_REQUEST) {
        return {...store, deletePlayList: payload}
    }
    return store
}