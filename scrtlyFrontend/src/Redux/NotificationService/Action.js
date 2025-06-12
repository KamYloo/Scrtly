import {dispatchAction} from "../api.js";
import {
    SEND_NOTIFICATION,
    GET_NOTIFICATIONS_ERROR,
    GET_NOTIFICATIONS_REQUEST,
    GET_NOTIFICATIONS_SUCCESS, DELETE_NOTIFICATION_REQUEST, DELETE_NOTIFICATION_SUCCESS, DELETE_NOTIFICATION_ERROR
} from "./ActionType.js";
import {POST_DELETE_ERROR, POST_DELETE_REQUEST, POST_DELETE_SUCCESS} from "../Post/ActionType.js";

export const getNotifications = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_NOTIFICATIONS_REQUEST, GET_NOTIFICATIONS_SUCCESS, GET_NOTIFICATIONS_ERROR, '/notifications/own', {
        method: 'GET',
        credentials: 'include',
    });
};

export const sendNotification = (notification) => {
    return {
        type: SEND_NOTIFICATION,
        payload: notification
    };
};

export const deleteNotification = (notificationId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_NOTIFICATION_REQUEST, DELETE_NOTIFICATION_SUCCESS, DELETE_NOTIFICATION_ERROR, `/notifications/delete/${notificationId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
};