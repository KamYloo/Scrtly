import {BASE_API_URL, dispatchAction} from "../../config/api"
import {
    REGISTER,
    LOGIN,
    REQUEST_USER,
    LOGOUT,
    FIND_USER_BY_ID_REQUEST,
    FIND_USER_BY_ID_ERROR,
    FOLLOW_USER_REQUEST,
    FOLLOW_USER_ERROR,
    SEARCH_USER_REQUEST,
    SEARCH_USER_ERROR,
    UPDATE_USER_REQUEST, UPDATE_USER_ERROR, REQUEST_USER_ERROR
} from "./ActionType"


export const register = (data) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/auth/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        })

        const resData = await res.json()
        if (resData.jwt)localStorage.setItem("token", resData.jwt)
        console.log("register ", resData)
        dispatch({ type: REGISTER, payload: resData })
    } catch (error) {
        console.log("catch error ", error)
    }
}


export const login = (data) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        })

        const resData = await res.json()
        console.log("login ", resData)
        if (resData.jwt)localStorage.setItem("token", resData.jwt)
        dispatch({ type: LOGIN, payload: resData })
    } catch (error) {
        console.log("catch error ", error)
    }
}

export const currentUser = () => async (dispatch) => {
    await dispatchAction(dispatch, REQUEST_USER, REQUEST_USER_ERROR, `/api/users/profile`, {
        method: 'GET',
    });
}

export const findUserById = (userId) => async (dispatch) => {
    await dispatchAction(dispatch, FIND_USER_BY_ID_REQUEST, FIND_USER_BY_ID_ERROR, `/api/users/${userId}`, {
        method: 'GET',
    });
}

export const followUser = (userId) => async (dispatch) => {
    await dispatchAction(dispatch, FOLLOW_USER_REQUEST, FOLLOW_USER_ERROR, `/api/users/${userId}/follow`, {
        method: 'PUT',
    });
}

export const searchUser = (data) => async (dispatch) => {
    await dispatchAction(dispatch, SEARCH_USER_REQUEST, SEARCH_USER_ERROR, `/api/users/search?name=${data.keyword}`, {
        method: 'GET',
    });
}

export const updateUser = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, UPDATE_USER_REQUEST, UPDATE_USER_ERROR, '/api/users/update', {
        method: 'PUT',
        body: formData
    });
}

export const logoutAction = () => async (dispatch) => {
    localStorage.removeItem("token")
    dispatch({ type: LOGOUT, payload: null})
    dispatch({ type: REQUEST_USER, payload: null})
}