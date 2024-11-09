import {dispatchAction} from "../../config/api.js";
import {
    CREATE_NEW_MESSAGE_ERROR,
    CREATE_NEW_MESSAGE_REQUEST,
    DELETE_MESSAGE_ERROR,
    DELETE_MESSAGE_REQUEST,
    GET_ALL_MESSAGES_ERROR, GET_ALL_MESSAGES_REQUEST
} from "./ActionType.js";

export const createChatMessage = (messageData) => async (dispatch) => {
    await dispatchAction(dispatch, CREATE_NEW_MESSAGE_REQUEST, CREATE_NEW_MESSAGE_ERROR, '/api/messages/create', {
        method: 'POST',
        body: JSON.stringify(messageData.data)
    });
}

export const getAllMessages = (reqData) => async (dispatch) => {
    await dispatchAction(dispatch, GET_ALL_MESSAGES_REQUEST, GET_ALL_MESSAGES_ERROR, `/api/messages/chat/${reqData.chatId}`, {
        method: 'GET',
        body: JSON.stringify(reqData.data)
    });
}

export const deleteChatMessage = (messageId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_MESSAGE_REQUEST, DELETE_MESSAGE_ERROR, `/api/messages/delete/${messageId}`, {
        method: 'DELETE',
    });
};