import {BASE_API_URL} from "../../config/api.js";
import {CREATE_NEW_MESSAGE, GET_ALL_MESSAGE} from "./ActionType.js";

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