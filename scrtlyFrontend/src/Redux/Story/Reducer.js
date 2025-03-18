import {
    GET_USERS_FOLLOWED_STORY_ERROR,
    GET_USERS_FOLLOWED_STORY_REQUEST, GET_USERS_FOLLOWED_STORY_SUCCESS,
    STORY_CREATE_FAILURE,
    STORY_CREATE_REQUEST,
    STORY_CREATE_SUCCESS
} from "./ActionType.js";


const initialValue = {
    loading: false,
    error: null,
    createdStory: null,
    stories: [],
}

export const storyReducer = (state = initialValue, {type, payload}) => {
    switch (type) {
        case STORY_CREATE_REQUEST:
            return { ...state, loading: true, error: null };
        case STORY_CREATE_SUCCESS:
            return { ...state, loading: false, createdStory: payload };
        case STORY_CREATE_FAILURE:
            return { ...state, loading: false, error: payload };

        case GET_USERS_FOLLOWED_STORY_REQUEST:
            return { ...state, loading: true, error: null };
        case GET_USERS_FOLLOWED_STORY_SUCCESS:
            return { ...state, loading: false, stories: payload };
        case GET_USERS_FOLLOWED_STORY_ERROR:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
}