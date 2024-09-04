import {CREATE_CHAT, GET_USERS_CHAT} from "./ActionType.js";


const initialValues = {
    chats:[],
    createdChat:null,
}

export const chatReducer = (store=initialValues,{type,payload}) => {

    if (type === CREATE_CHAT) {
        return {...store, createdChat: payload}
    }
    else if (type === GET_USERS_CHAT) {
        return {...store, chats: payload}
    }
    return store
}