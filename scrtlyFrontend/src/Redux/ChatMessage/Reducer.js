import {CREATE_NEW_MESSAGE, DELETE_MESSAGE_REQUEST, GET_ALL_MESSAGE} from "./ActionType.js";

const initialValues = {
    newMessage: null,
    messages: null,
    deletedMessage: null,
}

export const chatMessageReducer = (store=initialValues,{type,payload}) => {

    if (type === CREATE_NEW_MESSAGE)
        return {...store, newMessage: payload}
    else if (type === GET_ALL_MESSAGE)
        return {...store, messages: payload}
    else if (type === DELETE_MESSAGE_REQUEST) {
        return {...store, deletedMessage: payload}
    }

    return store
}