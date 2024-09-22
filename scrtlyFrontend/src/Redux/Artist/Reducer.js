import {FIND_ARTIST_BY_ID_REQUEST, GET_ALL_ARTISTS_REQUEST, GET_ARTIST_TRACKS_REQUEST} from "./ActionType.js";

const initialValue= {
    findArtist:null,
    artists: [],
    updateArtist: null,
    songs: []
}

export const artistReducer=(store=initialValue, {type,payload})=>{
    if (type === FIND_ARTIST_BY_ID_REQUEST) {
        return {...store, findArtist: payload}
    }
    else if (type === GET_ALL_ARTISTS_REQUEST) {
        return {...store, artists: payload}
    }
    else if (type === GET_ARTIST_TRACKS_REQUEST) {
        return {...store, songs: payload}
    }

    return store
}