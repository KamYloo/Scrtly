import {dispatchAction} from "../api.js";
import {
    BILLING_PORTAL_ERROR,
    BILLING_PORTAL_REQUEST,
    BILLING_PORTAL_SUCCESS, FETCH_SUBSCRIPTION_ERROR, FETCH_SUBSCRIPTION_REQUEST, FETCH_SUBSCRIPTION_SUCCESS,
    SUBSCRIBE_ERROR,
    SUBSCRIBE_REQUEST,
    SUBSCRIBE_SUCCESS
} from "./ActionType.js";

export const subscribeAction = (data) => async (dispatch) => {
    await dispatchAction(dispatch, SUBSCRIBE_REQUEST, SUBSCRIBE_SUCCESS, SUBSCRIBE_ERROR, `/subscription/create`, {
        method: 'POST',
        body: JSON.stringify(data),
        credentials: 'include',
    });
}

export const billingPortalAction = () => async (dispatch) => {
    await dispatchAction(dispatch, BILLING_PORTAL_REQUEST, BILLING_PORTAL_SUCCESS, BILLING_PORTAL_ERROR, `/billing-portal`, {
        method: 'POST',
        credentials: 'include',
    });
};

export const fetchSubscriptionAction = () => async (dispatch) => {
    await dispatchAction(dispatch, FETCH_SUBSCRIPTION_REQUEST, FETCH_SUBSCRIPTION_SUCCESS, FETCH_SUBSCRIPTION_ERROR, `/subscription/me`, {
            method: 'GET',
            credentials: 'include'
    });
};