import {
    BILLING_PORTAL_ERROR,
    BILLING_PORTAL_REQUEST,
    BILLING_PORTAL_SUCCESS,
    FETCH_SUBSCRIPTION_ERROR,
    FETCH_SUBSCRIPTION_REQUEST,
    FETCH_SUBSCRIPTION_SUCCESS,
    SUBSCRIBE_ERROR,
    SUBSCRIBE_REQUEST,
    SUBSCRIBE_SUCCESS
} from "./ActionType.js";


const initialValue = {
    loading: false,
    loadingButton: null,
    error: null,
    session: null,
    billingPortalUrl: null,
    subscription: null,
}

export const paymentReducer = (state = initialValue, { type, payload }) => {
    switch (type) {
        case FETCH_SUBSCRIPTION_REQUEST:
            return { ...state, loading: true, error: null };

        case SUBSCRIBE_REQUEST:
        case BILLING_PORTAL_REQUEST:
            return { ...state, loadingButton: true, error: null };

        case FETCH_SUBSCRIPTION_SUCCESS:
            return { ...state, loading: false, subscription: payload };

        case SUBSCRIBE_SUCCESS:
            return {
                ...state,
                loadingButton: false,
                session: payload.sessionId,
            };
        case BILLING_PORTAL_SUCCESS:
            return {
                ...state,
                loadingButton: false,
                billingPortalUrl: payload.url,
            };

        case FETCH_SUBSCRIPTION_ERROR:
            return { ...state, loading: false, error: payload };
        case SUBSCRIBE_ERROR:
        case BILLING_PORTAL_ERROR:
            return { ...state, loadingButton: false, error: payload };

        default:
            return state;
    }
};