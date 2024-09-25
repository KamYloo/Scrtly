import {
    DELETE_SONG_REQUEST
} from "./ActionType.js";

const initialValue= {
    deletedSong:null,
}

export const songReducer=(store=initialValue, {type,payload})=>{
    if (type === DELETE_SONG_REQUEST) {
        return {...store, deletedSong: payload}
    }
    return store
}