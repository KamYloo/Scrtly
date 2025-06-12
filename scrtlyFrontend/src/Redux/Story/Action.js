import {dispatchAction} from "../api.js";
import {
    GET_USERS_FOLLOWED_STORY_ERROR,
    GET_USERS_FOLLOWED_STORY_REQUEST, GET_USERS_FOLLOWED_STORY_SUCCESS,
    STORY_CREATE_FAILURE,
    STORY_CREATE_REQUEST, STORY_CREATE_SUCCESS
} from "./ActionType.js";

export const createStory = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, STORY_CREATE_REQUEST, STORY_CREATE_SUCCESS, STORY_CREATE_FAILURE, '/stories/create', {
        method: 'POST',
        body: formData,
        credentials: 'include',
    });
}

export const getFollowedUsersStory = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_USERS_FOLLOWED_STORY_REQUEST, GET_USERS_FOLLOWED_STORY_SUCCESS, GET_USERS_FOLLOWED_STORY_ERROR, '/stories/followed', {
        method: 'GET',
        credentials: 'include',
    });
}