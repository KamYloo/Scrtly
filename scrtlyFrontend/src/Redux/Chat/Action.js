import {BASE_API_URL} from "../../config/api.js";
import {CREATE_CHAT, DELETE_CHAT_ERROR, DELETE_CHAT_REQUEST, GET_USERS_CHAT} from "./ActionType.js";

export const createChat = (chatData) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/chats/chatRoom`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(chatData.data)
        })

        const data = await res.json()
        dispatch({ type: CREATE_CHAT, payload: data })
    } catch (error) {
        console.log("catch error ", error)
    }
}

export const getUsersChat = () => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/chats/user`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
        })

        const data = await res.json()
        dispatch({ type: GET_USERS_CHAT, payload: data })
    } catch (error) {
        console.log("catch error ", error)
    }
}

export const deleteChat = (chatRoomId) => async (dispatch) => {
    try {
        const response = await fetch(`${BASE_API_URL}/api/chats/delete/${chatRoomId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            },
        });

        const res = await response.json();
        console.log("Deleted ChatRoom", res)
        dispatch({ type: DELETE_CHAT_REQUEST, payload: res });
    } catch (error) {
        console.log('catch error', error);
        dispatch({ type: DELETE_CHAT_ERROR, payload: error.message });
    }
};