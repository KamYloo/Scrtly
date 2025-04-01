import {
    ADD_NEW_MESSAGE,
     DELETE_MESSAGE,
    EDIT_MESSAGE, GET_ALL_MESSAGES_ERROR,
    GET_ALL_MESSAGES_REQUEST, GET_ALL_MESSAGES_SUCCESS
} from "./ActionType.js";

const initialValues = {
    loading: false,
    error: null,
    newMessage: null,
    messages: [],
    deletedMessage: null,
    page: 0,
    last: false,
}

export const chatMessageReducer = (state=initialValues,{type,payload}) => {

    switch (type) {
        case GET_ALL_MESSAGES_REQUEST:
            return { ...state, loading: true, error: null };
        case GET_ALL_MESSAGES_SUCCESS:
            // eslint-disable-next-line no-case-declarations
            const newMessages = [...payload.content].reverse();
            return {
                ...state,
                loading: false,
                messages: payload.pageNumber === 0 ? newMessages : [...newMessages, ...state.messages],
                page: payload.pageNumber,
                last: payload.last,
            };

        case GET_ALL_MESSAGES_ERROR:
            return { ...state, loading: false, error: payload };

        case ADD_NEW_MESSAGE:
            return {
                ...state,
                messages: [...state.messages, payload],
            };

        case EDIT_MESSAGE:
            return {
                ...state,
                messages: state.messages.map(msg =>
                    msg.id === payload.id ? { ...msg, ...payload } : msg
                ),
            };

        case DELETE_MESSAGE:
            return {
                ...state,
                messages: state.messages.filter(message => message.id !== payload.id),
            };

        default:
            return state;
    }
}