import {dispatchAction} from "../api.js";
import {SUBSCRIBE_ERROR, SUBSCRIBE_REQUEST, SUBSCRIBE_SUCCESS} from "./ActionType.js";

export const subscribeAction = (data) => async (dispatch) => {
    await dispatchAction(dispatch, SUBSCRIBE_REQUEST, SUBSCRIBE_SUCCESS, SUBSCRIBE_ERROR, `/api/subscription/create`, {
        method: 'POST',
        body: JSON.stringify(data),
        credentials: 'include',
    });
}