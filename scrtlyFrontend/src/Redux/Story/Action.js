import {dispatchAction} from "../../config/api.js";
import {
    GET_USERS_FOLLOWED_STORY_ERROR,
    GET_USERS_FOLLOWED_STORY_REQUEST,
    STORY_CREATE_FAILURE,
    STORY_CREATE_REQUEST
} from "./ActionType.js";

export const createStory = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, STORY_CREATE_REQUEST, STORY_CREATE_FAILURE, '/api/stories/create', {
        method: 'POST',
        body: formData
    });
}

export const getFollowedUsersStory = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_USERS_FOLLOWED_STORY_REQUEST, GET_USERS_FOLLOWED_STORY_ERROR, '/api/stories/followed`', {
        method: 'GET',
    });
}