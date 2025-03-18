import {dispatchAction} from "../../config/api.js";
import {
    CREATE_NEW_MESSAGE_ERROR,
    CREATE_NEW_MESSAGE_REQUEST, CREATE_NEW_MESSAGE_SUCCESS,
    DELETE_MESSAGE_ERROR,
    DELETE_MESSAGE_REQUEST, DELETE_MESSAGE_SUCCESS,
    GET_ALL_MESSAGES_ERROR, GET_ALL_MESSAGES_REQUEST, GET_ALL_MESSAGES_SUCCESS
} from "./ActionType.js";

export const createChatMessage = (messageData) => async (dispatch) => {
    await dispatchAction(dispatch, CREATE_NEW_MESSAGE_REQUEST, CREATE_NEW_MESSAGE_SUCCESS, CREATE_NEW_MESSAGE_ERROR, '/api/messages/create', {
        method: 'POST',
        body: JSON.stringify(messageData.data),
        credentials: 'include',
    });
}

export const getAllMessages = (chatId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_ALL_MESSAGES_REQUEST,  GET_ALL_MESSAGES_SUCCESS, GET_ALL_MESSAGES_ERROR, `/api/messages/chat/${chatId}`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const deleteChatMessage = (messageId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_MESSAGE_REQUEST, DELETE_MESSAGE_SUCCESS, DELETE_MESSAGE_ERROR, `/api/messages/delete/${messageId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
};