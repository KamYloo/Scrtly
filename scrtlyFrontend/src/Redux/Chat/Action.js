import {dispatchAction} from "../../config/api.js";
import {
    CREATE_CHAT_ERROR,
    CREATE_CHAT_REQUEST,
    DELETE_CHAT_ERROR,
    DELETE_CHAT_REQUEST,
    GET_USERS_CHAT_ERROR, GET_USERS_CHAT_REQUEST
} from "./ActionType.js";

export const createChat = (userId) => async (dispatch) => {
    await dispatchAction(dispatch, CREATE_CHAT_REQUEST, CREATE_CHAT_ERROR, `/api/chats/create`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify([userId])
    });
};

export const getUserChats = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_USERS_CHAT_REQUEST, GET_USERS_CHAT_ERROR, '/api/chats/user', {
        method: 'GET',
        credentials: 'include',
    });
}

export const deleteChat = (chatRoomId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_CHAT_REQUEST, DELETE_CHAT_ERROR, `/api/chats/delete/${chatRoomId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
};