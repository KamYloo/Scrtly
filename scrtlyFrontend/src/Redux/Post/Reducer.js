import {GET_ALL_POSTS_REQUEST, POST_CREATE_REQUEST} from "./ActionType.js";

const initialValue= {
    createdPost:null,
    posts:[],
}

export const postReducer=(store=initialValue, {type,payload})=>{
    if (type === POST_CREATE_REQUEST) {
        return {...store, createdPost: payload}
    }else if(type === GET_ALL_POSTS_REQUEST) {
        return {...store, posts: payload}
    }

    return store
}