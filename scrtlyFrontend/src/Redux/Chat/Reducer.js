import {
    CREATE_CHAT_ERROR,
    CREATE_CHAT_REQUEST,
    CREATE_CHAT_SUCCESS, DELETE_CHAT_ERROR,
    DELETE_CHAT_REQUEST, DELETE_CHAT_SUCCESS, GET_USERS_CHAT_ERROR,
    GET_USERS_CHAT_REQUEST, GET_USERS_CHAT_SUCCESS
} from "./ActionType.js";

const initialValues = {
    loading: false,
    error: null,
    chats:[],
    createdChat:null,
    deletedChat:null,
}

export const chatReducer = (state = initialValues, { type, payload }) => {
    switch (type) {
        case CREATE_CHAT_REQUEST:
        case GET_USERS_CHAT_REQUEST:
        case DELETE_CHAT_REQUEST:
            return { ...state, loading: true, error: null };

        case CREATE_CHAT_SUCCESS:
            return {
                ...state,
                loading: false,
                createdChat: payload,
                chats: [payload, ...state.chats]
            };

        case GET_USERS_CHAT_SUCCESS:
            return { ...state, loading: false, chats: payload };

        case DELETE_CHAT_SUCCESS:
            return {
                ...state,
                loading: false,
                deletedChat: payload,
                chats: state.chats.filter(chat => chat.id !== payload)
            };

        case CREATE_CHAT_ERROR:
        case GET_USERS_CHAT_ERROR:
        case DELETE_CHAT_ERROR:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
};