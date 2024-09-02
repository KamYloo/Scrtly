
import {BASE_API_URL} from "../../config/api.js";
import {POST_CREATE_REQUEST} from "./ActionType.js";

export const createPost = (formData) => async (dispatch) => {

    try {
        const response = await fetch(`${BASE_API_URL}/api/posts/create`,  {
            method: 'POST',
            headers : {
                Authorization: `Bearer ${formData.get('token')}`
            },
            body: formData
        })

        const res = await response.json()
        console.log("created Post", res)
        dispatch({type: POST_CREATE_REQUEST, payload: res})
    }catch(err) {
        console.log("catch error " + err)
    }
}