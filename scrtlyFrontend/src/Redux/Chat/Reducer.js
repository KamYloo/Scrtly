import {CREATE_CHAT_REQUEST, DELETE_CHAT_REQUEST, GET_USERS_CHAT_REQUEST} from "./ActionType.js";

const initialValues = {
    chats:[],
    createdChat:null,
    deletedChat:null,
}

export const chatReducer = (store=initialValues,{type,payload}) => {

    if (type === CREATE_CHAT_REQUEST) {
        return {...store, createdChat: payload}
    }
    else if (type === GET_USERS_CHAT_REQUEST) {
        return {...store, chats: payload}
    }
    else if (type === DELETE_CHAT_REQUEST) {
        return {...store, deletedChat: payload}
    }
    return store
}