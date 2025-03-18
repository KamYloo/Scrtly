import {
    CREATE_NEW_MESSAGE_ERROR,
    CREATE_NEW_MESSAGE_REQUEST, CREATE_NEW_MESSAGE_SUCCESS, DELETE_MESSAGE_ERROR,
    DELETE_MESSAGE_REQUEST, DELETE_MESSAGE_SUCCESS, GET_ALL_MESSAGES_ERROR,
    GET_ALL_MESSAGES_REQUEST, GET_ALL_MESSAGES_SUCCESS
} from "./ActionType.js";

const initialValues = {
    loading: false,
    error: null,
    newMessage: null,
    messages: [],
    deletedMessage: null,
}

export const chatMessageReducer = (state=initialValues,{type,payload}) => {

    switch (type) {
        case CREATE_NEW_MESSAGE_REQUEST:
            return { ...state, loading: true, error: null };
        case CREATE_NEW_MESSAGE_SUCCESS:
            return { ...state, loading: false, newMessage: payload };
        case CREATE_NEW_MESSAGE_ERROR:
            return { ...state, loading: false, error: payload };

        case GET_ALL_MESSAGES_REQUEST:
            return { ...state, loading: true, error: null };
        case GET_ALL_MESSAGES_SUCCESS:
            return { ...state, loading: false, messages: payload };
        case GET_ALL_MESSAGES_ERROR:
            return { ...state, loading: false, error: payload };

        case DELETE_MESSAGE_REQUEST:
            return { ...state, loading: true, error: null };
        case DELETE_MESSAGE_SUCCESS:
            return { ...state, loading: false, deletedMessage: payload };
        case DELETE_MESSAGE_ERROR:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
}