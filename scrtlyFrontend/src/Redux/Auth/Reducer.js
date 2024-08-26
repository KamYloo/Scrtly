import {LOGIN, REGISTER, REQUEST_USER, SEARCH_USER, UPDATE_USER} from "./ActionType.js";

const initialValue= {
    login:null,
    register:null,
    reqUser:null,
    searchResults:null,
}

export const authReducer=(store=initialValue, {type,payload})=>{
    if (type === REGISTER) {
        return {...store, register: payload}
    }
    else if (type === LOGIN) {
        return {...store, login: payload}
    }
    else if (type === REQUEST_USER) {
        return {...store, reqUser: payload}
    }
    else if (type === UPDATE_USER) {
        return {...store, updatedUser: payload}
    }
    else if (type === SEARCH_USER) {
        return {...store, searchResults: payload}
    }
    return store
}