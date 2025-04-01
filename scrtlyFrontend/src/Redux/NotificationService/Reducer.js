import {
    GET_NOTIFICATIONS_REQUEST,
    GET_NOTIFICATIONS_SUCCESS,
    GET_NOTIFICATIONS_ERROR,
    SEND_NOTIFICATION, DELETE_NOTIFICATION_REQUEST, DELETE_NOTIFICATION_SUCCESS, DELETE_NOTIFICATION_ERROR
} from "./ActionType.js";

const initialState = {
    loading: false,
    error: null,
    notifications: [],
    page: 0,
    last: false,
    deletedNotification:null,
};

export const notificationReducer = (state = initialState, { type, payload, page }) => {
    switch (type) {
        case GET_NOTIFICATIONS_REQUEST:
            return { ...state, loading: true, error: null };
        case GET_NOTIFICATIONS_SUCCESS:
            return {
                ...state,
                loading: false,
                notifications: page === 0 ? payload.content : [...new Map([...state.notifications, ...payload.content].map(item => [item.id, item])).values()],
                page,
                last: payload.last,
            };
        case GET_NOTIFICATIONS_ERROR:
            return { ...state, loading: false, error: payload };
        case SEND_NOTIFICATION:
            return {
                ...state,
                notifications: [payload, ...state.notifications.filter(n => n.id !== payload.id)],
            };
        case DELETE_NOTIFICATION_REQUEST:
            return { ...state, loading: true };
        case DELETE_NOTIFICATION_SUCCESS:
            return {
                ...state,
                loading: false,
                notifications: state.notifications.filter(n => n.id !== payload),
            };
        case DELETE_NOTIFICATION_ERROR:
            return { ...state, loading: false, error: payload };
        default:
            return state;

    }
};
