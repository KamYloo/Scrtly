import {STORY_CREATE_REQUEST} from "./ActionType.js";

const initialValue = {
    createdStory: null,
}

export const storyReducer = (store = initialValue, {type, payload}) => {
    if (type === STORY_CREATE_REQUEST) {
        return {...store, createdStory: payload}
    }

    return store
}