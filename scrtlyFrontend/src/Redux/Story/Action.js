import {BASE_API_URL} from "../../config/api.js";
import {STORY_CREATE_FAILURE, STORY_CREATE_REQUEST} from "./ActionType.js";

export const createStory = (formData) => async (dispatch) => {

    try {
        const response = await fetch(`${BASE_API_URL}/api/stories/create`,  {
            method: 'POST',
            headers : {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            body: formData
        })

        const story = await response.json()
        console.log("created Story", story)
        dispatch({type: STORY_CREATE_REQUEST, payload: story})
    }catch(error) {
        console.log("catch error " + error)
        dispatch({ type: STORY_CREATE_FAILURE, payload: error.message });
    }
}