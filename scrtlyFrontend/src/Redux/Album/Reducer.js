import {
    CREATE_ALBUM_REQUEST, FIND_ALBUM_REQUEST,
    GET_ALL_ALBUMS_REQUEST,
} from "./ActionType.js";

const initialValue= {
    findAlbum:null,
    albums: [],
    createAlbum : null,
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

    return store
}