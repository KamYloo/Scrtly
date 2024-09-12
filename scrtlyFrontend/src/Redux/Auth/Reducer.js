import {
    FIND_USER_BY_ID_REQUEST,
    FOLLOW_USER_REQUEST,
    LOGIN,
    REGISTER,
    REQUEST_USER,
    SEARCH_USER,
    UPDATE_USER
} from "./ActionType.js";

const initialValue= {
    login:null,
    register:null,
    reqUser:null,
    searchResults:null,
    updateUser:null,
    findUser:null,
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
        return {...store, reqUser: payload}
    }
    else if (type === SEARCH_USER) {
        return {...store, searchResults: payload}
    }
    else if (type === FIND_USER_BY_ID_REQUEST) {
        return {...store, findUser: payload}
    }
    else if (type === FOLLOW_USER_REQUEST) {
        return {...store, findUser: payload}
    }
    return store
}