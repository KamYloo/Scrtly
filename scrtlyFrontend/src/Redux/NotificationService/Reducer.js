import {
    GET_NOTIFICATIONS_REQUEST,
    GET_NOTIFICATIONS_SUCCESS,
    GET_NOTIFICATIONS_ERROR,
    ADD_NOTIFICATION
} from "./ActionType.js";

const initialState = {
    loading: false,
    error: null,
    notifications: [],
    page: 0,
    last: false,
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
        case ADD_NOTIFICATION:
            return {
                ...state,
                notifications: [payload, ...state.notifications.filter(n => n.id !== payload.id)],
            };
        default:
            return state;
    }
};
