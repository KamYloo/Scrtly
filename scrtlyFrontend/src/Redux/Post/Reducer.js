import {GET_ALL_POSTS_REQUEST, LIKE_POST_REQUEST, POST_CREATE_REQUEST, POST_DELETE_REQUEST} from "./ActionType.js";

const initialValue= {
    createdPost:null,
    deletedPost:null,
    posts:[],
    likedPost: null,
}

export const postReducer=(store=initialValue, {type,payload})=>{
    if (type === POST_CREATE_REQUEST) {
        return {...store, createdPost: payload}
    }else if(type === GET_ALL_POSTS_REQUEST) {
        return {...store, posts: payload}
    }else if(type === POST_DELETE_REQUEST) {
        return {...store, deletedPost: payload}
    }else if (type === LIKE_POST_REQUEST) {
        return {...store, likedPost: payload}
    }

    return store
}