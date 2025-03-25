import {dispatchAction} from "../../config/api.js";
import {
    ADD_NOTIFICATION,
    GET_NOTIFICATIONS_ERROR,
    GET_NOTIFICATIONS_REQUEST,
    GET_NOTIFICATIONS_SUCCESS
} from "./ActionType.js";

export const getNotifications = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_NOTIFICATIONS_REQUEST, GET_NOTIFICATIONS_SUCCESS, GET_NOTIFICATIONS_ERROR, '/api/notifications/own', {
        method: 'GET',
        credentials: 'include',
    });
};

export const addNotification = (notification) => {
    return {
        type: ADD_NOTIFICATION,
        payload: notification
    };
};
