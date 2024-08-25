import {CREATE_NEW_MESSAGE, GET_ALL_MESSAGE} from "./ActionType.js";

const initialValues = {
    messages: null,
    newMessage: null,
}

export const chatMessageReducer = (store=initialValues,{type,payload}) => {

    if (type === CREATE_NEW_MESSAGE)
        return {...store, newMessage: payload}
    else if (type === GET_ALL_MESSAGE)
        return {...store, chatMessages: payload}

    return store
}