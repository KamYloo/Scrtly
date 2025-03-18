import {
    GET_ALL_POSTS_ERROR,
    GET_ALL_POSTS_REQUEST, GET_ALL_POSTS_SUCCESS, GET_POSTS_BY_USERID_ERROR,
    GET_POSTS_BY_USERID_REQUEST, GET_POSTS_BY_USERID_SUCCESS, LIKE_POST_ERROR,
    LIKE_POST_REQUEST, LIKE_POST_SUCCESS, POST_CREATE_ERROR,
    POST_CREATE_REQUEST, POST_CREATE_SUCCESS, POST_DELETE_ERROR,
    POST_DELETE_REQUEST, POST_DELETE_SUCCESS, UPDATE_POST_ERROR, UPDATE_POST_REQUEST, UPDATE_POST_SUCCESS
} from "./ActionType.js";

const initialValue= {
    loading: false,
    error: null,
    createdPost:null,
    deletedPost:null,
    posts: {
        content: [],
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
    },
    likedPost: null,
}

export const postReducer=(state=initialValue, {type,payload})=>{
    switch (type) {
        case POST_CREATE_REQUEST:
            return { ...state, loading: true, error: null };
        case POST_CREATE_SUCCESS:
            return { ...state, loading: false, createdPost: payload };
        case POST_CREATE_ERROR:
            return { ...state, loading: false, error: payload };

        case UPDATE_POST_REQUEST:
            return { ...state, loading: true, error: null };
        case UPDATE_POST_SUCCESS:
            return { ...state, loading: false, createdPost: payload };
        case UPDATE_POST_ERROR:
            return { ...state, loading: false, error: payload };

        case GET_ALL_POSTS_REQUEST:
            return { ...state, loading: true, error: null };
        case GET_ALL_POSTS_SUCCESS:
            return { ...state, loading: false, posts: payload };
        case GET_ALL_POSTS_ERROR:
            return { ...state, loading: false, error: payload };

        case POST_DELETE_REQUEST:
            return { ...state, loading: true, error: null };
        case POST_DELETE_SUCCESS:
            return { ...state, loading: false, deletedPost: payload };
        case POST_DELETE_ERROR:
            return { ...state, loading: false, error: payload };

        case LIKE_POST_REQUEST:
            return { ...state, loading: true, error: null };
        case LIKE_POST_SUCCESS:
            return { ...state, loading: false, likedPost: payload };
        case LIKE_POST_ERROR:
            return { ...state, loading: false, error: payload };

        case GET_POSTS_BY_USERID_REQUEST:
            return { ...state, loading: true, error: null };
        case GET_POSTS_BY_USERID_SUCCESS:
            return { ...state, loading: false, posts: payload };
        case GET_POSTS_BY_USERID_ERROR:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
}