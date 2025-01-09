import {
    FIND_ARTIST_BY_ID_REQUEST,
    GET_ALL_ARTISTS_REQUEST,
    GET_ARTIST_TRACKS_REQUEST
} from "./ActionType.js";
import {FOLLOW_USER_REQUEST} from "../AuthService/ActionType.js";

const initialValue= {
    findArtist:null,
    artists: {
        content: [],
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
    },
    updateArtist: null,
    songs: {
        content: [],
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
    },
    follow:null
}

export const artistReducer=(store=initialValue, {type,payload})=>{
    if (type === FIND_ARTIST_BY_ID_REQUEST) {
        return {...store, findArtist: payload}
    }
    else if (type === FOLLOW_USER_REQUEST) {
        return {...store, follow: payload}
    }
    else if (type === GET_ALL_ARTISTS_REQUEST) {
        return {...store, artists: payload}
    }
    else if (type === GET_ARTIST_TRACKS_REQUEST) {
        return {...store, songs: payload}
    }
    return store
}