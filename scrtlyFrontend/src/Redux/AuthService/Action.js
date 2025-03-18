import {BASE_API_URL, dispatchAction} from "../../config/api"
import {
    REQUEST_USER,
    FOLLOW_USER_REQUEST,
    FOLLOW_USER_ERROR,
    SEARCH_USER_REQUEST,
    SEARCH_USER_ERROR,
    UPDATE_USER_REQUEST,
    UPDATE_USER_ERROR,
    REQUEST_USER_ERROR,
    LOGIN_REQUEST,
    LOGIN_ERROR,
    REGISTER_REQUEST,
    REGISTER_ERROR,
    LOGOUT_REQUEST,
    LOGOUT_ERROR,
    FIND_USER_REQUEST,
    FIND_USER_ERROR,
    REGISTER_SUCCESS,
    LOGIN_SUCCESS,
    REQUEST_USER_SUCCESS, FIND_USER_SUCCESS, SEARCH_USER_SUCCESS, UPDATE_USER_SUCCESS, FOLLOW_USER_SUCCESS
} from "./ActionType"


export const registerAction = (data) => async (dispatch) => {
    await dispatchAction(dispatch, REGISTER_REQUEST, REGISTER_SUCCESS, REGISTER_ERROR, '/api/auth/register',
        {
            method: 'POST',
            body: JSON.stringify(data),
        }
    );
};

export const loginAction = (data) => async (dispatch) => {
    await dispatchAction(dispatch, LOGIN_REQUEST, LOGIN_SUCCESS, LOGIN_ERROR, '/api/auth/login',
        {
            method: 'POST',
            body: JSON.stringify(data),
            credentials: 'include',
        }
    );
};

export const logoutAction = () => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/auth/logout`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            credentials: 'include'
        })

        const resData = await res.text()
        localStorage.removeItem("refreshToken")
        localStorage.removeItem("user")
        dispatch({ type: LOGOUT_REQUEST, payload: resData })

    } catch (error) {
        console.log("catch error ", error)
        dispatch({ type: LOGOUT_ERROR, payload: error.message });
    }
}

export const currentUser = () => async (dispatch) => {
    await dispatchAction(dispatch, REQUEST_USER, REQUEST_USER_SUCCESS, REQUEST_USER_ERROR, `/api/users/profile`, {
        method: 'GET',
    });
}

export const findUser = (nickName) => async (dispatch) => {
    await dispatchAction(dispatch, FIND_USER_REQUEST, FIND_USER_SUCCESS, FIND_USER_ERROR, `/api/user/profile/${nickName}`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const followUser = (userId) => async (dispatch) => {
    await dispatchAction(dispatch, FOLLOW_USER_REQUEST, FOLLOW_USER_SUCCESS, FOLLOW_USER_ERROR, `/api/user/follow/${userId}`, {
        method: 'PUT',
        credentials: 'include',
    });
}

export const searchUser = (data) => async (dispatch) => {
    await dispatchAction(dispatch, SEARCH_USER_REQUEST, SEARCH_USER_SUCCESS, SEARCH_USER_ERROR, `/api/user/search?name=${data.keyword}`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const updateUser = (formData) => async (dispatch) => {
    return await dispatchAction(dispatch, UPDATE_USER_REQUEST, UPDATE_USER_SUCCESS, UPDATE_USER_ERROR, '/api/user/profile/edit', {
        method: 'PUT',
        body: formData,
        credentials: 'include',
    });
}