import {
    SUBSCRIBE_ERROR,
    SUBSCRIBE_REQUEST, SUBSCRIBE_SUCCESS
} from "./ActionType.js";


const initialValue = {
    loading: false,
    error: null,
    session: null,
}

export const paymentReducer = (state = initialValue, {type, payload}) => {
    switch (type) {
        case SUBSCRIBE_REQUEST:
            return { ...state, loading: true, error: null };
        case SUBSCRIBE_SUCCESS:
            return {
                ...state,
                loading: false,
                session: payload.sessionId ,
            };
        case SUBSCRIBE_ERROR:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
}