import {GET_USERS_FOLLOWED_STORY_REQUEST, STORY_CREATE_REQUEST} from "./ActionType.js";


const initialValue = {
    createdStory: null,
    stories: [],
}

export const storyReducer = (store = initialValue, {type, payload}) => {
    if (type === STORY_CREATE_REQUEST) {
        return {...store, createdStory: payload}
    } else if (type === GET_USERS_FOLLOWED_STORY_REQUEST) {
        return {...store, stories: payload}
    }

    return store
}