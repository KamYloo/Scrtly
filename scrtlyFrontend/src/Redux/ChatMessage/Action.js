import {dispatchAction} from "../../config/api.js";
import {
    GET_ALL_MESSAGES_ERROR, GET_ALL_MESSAGES_REQUEST, GET_ALL_MESSAGES_SUCCESS
} from "./ActionType.js";

export const getAllMessages = (chatId, page = 0) => async (dispatch) => {
    await dispatchAction(dispatch, GET_ALL_MESSAGES_REQUEST,  GET_ALL_MESSAGES_SUCCESS, GET_ALL_MESSAGES_ERROR, `/api/messages/chat/${chatId}?page=${page}`, {
        method: 'GET',
        credentials: 'include',
    });
}

