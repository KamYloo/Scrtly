
import {CREATE_COMMENT_REQUEST, DELETE_COMMENT_REQUEST, GET_POST_COMMENT_REQUEST} from "./ActionType.js";

const initialValue = {
    createdComment:null,
    comments: [],
    deletedComment:null,
}

export const commentReducer=(store=initialValue, {type,payload})=>{
    if(type === CREATE_COMMENT_REQUEST) {
        return {...store, createdComment: payload}
    }
    else if(type === GET_POST_COMMENT_REQUEST) {
        return {...store, comments: payload}
    }
    else if(type === DELETE_COMMENT_REQUEST) {
        return {...store, deletedComment: payload}
    }

    return store
}