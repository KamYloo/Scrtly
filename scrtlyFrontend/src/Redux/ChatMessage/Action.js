import {BASE_API_URL} from "../../config/api.js";
import {CREATE_NEW_MESSAGE, DELETE_MESSAGE_ERROR, DELETE_MESSAGE_REQUEST, GET_ALL_MESSAGE} from "./ActionType.js";

export const createChatMessage = (messageData) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/messages/create`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(messageData.data)
        })

        const data = await res.json()
        console.log("created ChatMessage", data)
        dispatch({ type: CREATE_NEW_MESSAGE, payload: data })
    } catch (error) {
        console.log("catch error ", error)
    }
}

export const getAllMessages = (reqData) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/messages/chat/${reqData.chatId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(reqData.data)
        })

        const data = await res.json()
        dispatch({ type: GET_ALL_MESSAGE, payload: data })
    } catch (error) {
        console.log("catch error ", error)
    }
}

export const deleteChatMessage = (messageId) => async (dispatch) => {
    try {
        const response = await fetch(`${BASE_API_URL}/api/messages/delete/${messageId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            },
        });

        const res = await response.json();
        console.log("Deleted ChatRoom", res)
        dispatch({ type: DELETE_MESSAGE_REQUEST, payload: res });
    } catch (error) {
        console.log('catch error', error);
        dispatch({ type: DELETE_MESSAGE_ERROR, payload: error.message });
    }
};