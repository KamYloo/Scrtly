import {
    FIND_USER_REQUEST,
    FOLLOW_USER_REQUEST,
    LOGIN_REQUEST, LOGOUT_REQUEST,
    REGISTER_REQUEST,
    REQUEST_USER,
    SEARCH_USER_REQUEST,
    UPDATE_USER_REQUEST
} from "./ActionType.js";

const initialValue= {
    login:null,
    register:null,
    logout:null,
   /* reqUser:null,
    searchResults:null,
    updateUser:null,
    findUser:null,*/
}

export const authReducer=(store=initialValue, {type,payload})=>{
    if (type === REGISTER_REQUEST) {
        return {...store, register: payload}
    }
    else if (type === LOGIN_REQUEST) {
        return {...store, login: payload}
    }
    else if (type === LOGOUT_REQUEST) {
        return {...store, logout: payload, login: null}
    }
    else if (type === REQUEST_USER) {
        return {...store, reqUser: payload}
    }
    else if (type === UPDATE_USER_REQUEST) {
        return {...store, reqUser: payload}
    }
    else if (type === SEARCH_USER_REQUEST) {
        return {...store, searchResults: payload}
    }
    else if (type === FIND_USER_REQUEST) {
        return {...store, findUser: payload}
    }
    else if (type === FOLLOW_USER_REQUEST) {
        return {...store, findUser: payload}
    }
    return store
}