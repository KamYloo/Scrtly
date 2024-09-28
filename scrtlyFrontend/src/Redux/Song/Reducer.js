import {
    DELETE_SONG_REQUEST, SEARCH_SONG_REQUEST
} from "./ActionType.js";

const initialValue= {
    deletedSong:null,
    searchResults: null,
}

export const songReducer=(store=initialValue, {type,payload})=>{
    if (type === DELETE_SONG_REQUEST) {
        return {...store, deletedSong: payload}
    } else if (type === SEARCH_SONG_REQUEST) {
        return {...store, searchResults: payload}
    }
    return store
}