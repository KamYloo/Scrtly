import {BASE_API_URL} from "../../config/api.js";
import {
    CREATE_COMMENT_FAIL,
    CREATE_COMMENT_REQUEST, DELETE_COMMENT_FAIL, DELETE_COMMENT_REQUEST,
    GET_POST_COMMENT_REQUEST,
    GET_POST_COMMENTS_FAIL, Like_COMMENT_FAIL, Like_COMMENT_REQUEST,
} from "./ActionType.js";

export const createComment = (data) => async (dispatch) => {

    try {
        const response = await fetch(`${BASE_API_URL}/api/comments/create/${data.postId}`,  {
            method: 'POST',
            headers : {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(data)
        })

        const comment = await response.json();
        console.log("created comment", comment);
        dispatch({ type: CREATE_COMMENT_REQUEST, payload: comment });
    } catch (error) {
        console.log('catch error', error);
        dispatch({ type: CREATE_COMMENT_FAIL, payload: error.message });
    }
}

export const getAllPostComments = (reqData) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/comments/get/${reqData.postId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(reqData.data)
        })

        const data = await res.json()
        console.log("getPostComments ", data)
        dispatch({ type: GET_POST_COMMENT_REQUEST, payload: data })
    } catch (error) {
        console.log("catch error ", error)
        dispatch({ type: GET_POST_COMMENTS_FAIL, payload: error.message });
    }
}

export const likeComment = (commentId) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/api/comment/${commentId}/like`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(commentId)
        })

        const like = await res.json()
        console.log("LikedComment ", like)
        dispatch({ type: Like_COMMENT_REQUEST, payload: like })
    } catch (error) {
        console.log("catch error ", error)
        dispatch({ type: Like_COMMENT_FAIL, payload: error.message });
    }
}

export const deleteComment = (commentId) => async (dispatch) => {
    try {
        const response = await fetch(`${BASE_API_URL}/api/posts/delete/${commentId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            },
        });

        const res = await response.json();
        console.log("Deleted Comment", res)
        dispatch({ type: DELETE_COMMENT_REQUEST, payload: res });
    } catch (error) {
        console.log('catch error', error);
        dispatch({ type: DELETE_COMMENT_FAIL, payload: error.message });
    }
};