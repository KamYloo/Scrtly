import {
    FIND_USER_ERROR,
    FIND_USER_REQUEST, FIND_USER_SUCCESS, FOLLOW_USER_ERROR,
    FOLLOW_USER_REQUEST, FOLLOW_USER_SUCCESS, LOGIN_ERROR,
    LOGIN_REQUEST, LOGIN_SUCCESS, LOGOUT_ERROR, LOGOUT_REQUEST, LOGOUT_SUCCESS, REGISTER_ERROR,
    REGISTER_REQUEST, REGISTER_SUCCESS,
    REQUEST_USER, REQUEST_USER_ERROR, REQUEST_USER_SUCCESS, SEARCH_USER_ERROR,
    SEARCH_USER_REQUEST, SEARCH_USER_SUCCESS, UPDATE_USER_ERROR,
    UPDATE_USER_REQUEST, UPDATE_USER_SUCCESS
} from "./ActionType.js";

const initialValue= {
    loading: false,
    error: null,
    login:null,
    register:null,
    logout:null,
    reqUser:null,
    searchResults:null,
    updateUser:null,
    findUser:null,
}

export const authReducer=(state=initialValue, {type,payload})=>{
    switch(type) {
        case REGISTER_REQUEST:
            return { ...state, loading: true, error: null };
        case REGISTER_SUCCESS:
            return { ...state, loading: false, registerResponse: payload };
        case REGISTER_ERROR:
            return { ...state, loading: false, error: payload };

        case LOGIN_REQUEST:
            return { ...state, loading: true, error: null };
        case LOGIN_SUCCESS:
            return { ...state, loading: false, loginResponse: payload, user: payload.user };
        case LOGIN_ERROR:
            return { ...state, loading: false, error: payload };

        case LOGOUT_REQUEST:
            return { ...state, loading: true, error: null };
        case LOGOUT_SUCCESS:
            return { ...state, loading: false, logoutResponse: payload, user: null };
        case LOGOUT_ERROR:
            return { ...state, loading: false, error: payload };

        case REQUEST_USER:
            return { ...state, loading: true, error: null };
        case REQUEST_USER_SUCCESS:
            return { ...state, loading: false, reqUser: payload };
        case REQUEST_USER_ERROR:
            return { ...state, loading: false, error: payload };

        case SEARCH_USER_REQUEST:
            return { ...state, loading: true, error: null };
        case SEARCH_USER_SUCCESS:
            return { ...state, loading: false, searchResults: payload };
        case SEARCH_USER_ERROR:
            return { ...state, loading: false, error: payload };

        case UPDATE_USER_REQUEST:
            return { ...state, loading: true, error: null };
        case UPDATE_USER_SUCCESS:
            return { ...state, loading: false, user: payload };
        case UPDATE_USER_ERROR:
            return { ...state, loading: false, error: payload };

        case FIND_USER_REQUEST:
            return { ...state, loading: true, error: null };
        case FIND_USER_SUCCESS:
            return { ...state, loading: false, findUser: payload };
        case FIND_USER_ERROR:
            return { ...state, loading: false, error: payload };

        case FOLLOW_USER_REQUEST:
            return { ...state, loading: true, error: null };
        case FOLLOW_USER_SUCCESS:
            return { ...state, loading: false, findUser: payload };
        case FOLLOW_USER_ERROR:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
}