
import {BASE_API_URL} from "../../config/api.js";
import {GET_ALL_POSTS_ERROR, GET_ALL_POSTS_REQUEST, POST_CREATE_REQUEST} from "./ActionType.js";

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

export const getAllPosts = () => async (dispatch) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${BASE_API_URL}/api/posts/getAll`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        });


        const posts = await response.json();
        dispatch({ type: GET_ALL_POSTS_REQUEST, payload: posts });
    } catch (error) {
        console.log('catch error', error);
        dispatch({ type: GET_ALL_POSTS_ERROR, payload: error.message });
    }
};