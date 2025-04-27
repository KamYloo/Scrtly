import {dispatchAction} from "../../config/api.js";
import {
    CREATE_CHAT_ERROR,
    CREATE_CHAT_REQUEST, CREATE_CHAT_SUCCESS,
    DELETE_CHAT_ERROR,
    DELETE_CHAT_REQUEST, DELETE_CHAT_SUCCESS,
    GET_USERS_CHAT_ERROR, GET_USERS_CHAT_REQUEST, GET_USERS_CHAT_SUCCESS
} from "./ActionType.js";

export const createChat = (userIds, chatRoomName = null) => async (dispatch) => {
    await dispatchAction(dispatch, CREATE_CHAT_REQUEST, CREATE_CHAT_SUCCESS, CREATE_CHAT_ERROR, `/chats/create`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            userIds,
            chatRoomName
        })
    });
};

export const getUserChats = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_USERS_CHAT_REQUEST, GET_USERS_CHAT_SUCCESS, GET_USERS_CHAT_ERROR, '/chats/user', {
        method: 'GET',
        credentials: 'include',
    });
}

export const deleteChat = (chatRoomId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_CHAT_REQUEST, DELETE_CHAT_SUCCESS, DELETE_CHAT_ERROR, `/chats/delete/${chatRoomId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
};