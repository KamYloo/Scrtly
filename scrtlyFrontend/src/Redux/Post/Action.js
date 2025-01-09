import {dispatchAction} from "../../config/api.js";
import {
    GET_ALL_POSTS_ERROR,
    GET_ALL_POSTS_REQUEST,
    GET_POSTS_BY_USERID_ERROR,
    GET_POSTS_BY_USERID_REQUEST,
    LIKE_POST_ERROR,
    LIKE_POST_REQUEST,
    POST_CREATE_ERROR,
    POST_CREATE_REQUEST,
    POST_DELETE_ERROR,
    POST_DELETE_REQUEST, UPDATE_POST_ERROR, UPDATE_POST_REQUEST
} from "./ActionType.js";

export const createPost = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, POST_CREATE_REQUEST, POST_CREATE_ERROR, '/api/posts/create', {
        method: 'POST',
        body: formData,
        credentials: 'include',
    });
}

export const updatePost = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, UPDATE_POST_REQUEST, UPDATE_POST_ERROR, `/api/posts/update/${formData.get("postId")}`, {
        method: 'PUT',
        body: formData,
        credentials: 'include',
    });
}

export const getAllPosts = () => async (dispatch) => {
    await dispatchAction(dispatch, GET_ALL_POSTS_REQUEST, GET_ALL_POSTS_ERROR, '/api/posts/all', {
        method: 'GET',
        credentials: 'include',
    });
};

export const getPostsByUser = (userId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_POSTS_BY_USERID_REQUEST, GET_POSTS_BY_USERID_ERROR, `/api/posts/all/${userId}`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const likePost = (postId) => async (dispatch) => {
    await dispatchAction(dispatch, LIKE_POST_REQUEST, LIKE_POST_ERROR, `/api/post/${postId}/like`, {
        method: 'PUT',
        credentials: 'include',
    });
}

export const deletePost = (postId) => async (dispatch) => {
    await dispatchAction(dispatch, POST_DELETE_REQUEST, POST_DELETE_ERROR, `/api/posts/delete/${postId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
};
