import {POST_CREATE_REQUEST} from "./ActionType.js";

const initialValue= {
    createdPost:null,
}

export const PostReducer=(store=initialValue, {type,payload})=>{
    if (type === POST_CREATE_REQUEST) {
        return {...store, createdPost: payload}
    }

    return store
}