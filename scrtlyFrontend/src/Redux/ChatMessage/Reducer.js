import {
    CREATE_NEW_MESSAGE_REQUEST,
    DELETE_MESSAGE_REQUEST,
    GET_ALL_MESSAGES_REQUEST
} from "./ActionType.js";

const initialValues = {
    newMessage: null,
    messages: [],
    deletedMessage: null,
}

export const chatMessageReducer = (store=initialValues,{type,payload}) => {

    if (type === CREATE_NEW_MESSAGE_REQUEST)
        return {...store, newMessage: payload}
    else if (type === GET_ALL_MESSAGES_REQUEST)
        return {...store, messages: payload}
    else if (type === DELETE_MESSAGE_REQUEST) {
        return {...store, deletedMessage: payload}
    }

    return store
}