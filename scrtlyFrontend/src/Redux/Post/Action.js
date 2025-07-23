import {dispatchAction} from "../api.js";
import {
    GET_ALL_POSTS_ERROR,
    GET_ALL_POSTS_REQUEST, GET_ALL_POSTS_SUCCESS,
    GET_POSTS_BY_USERID_ERROR,
    GET_POSTS_BY_USERID_REQUEST, GET_POSTS_BY_USERID_SUCCESS,
    LIKE_POST_ERROR,
    LIKE_POST_REQUEST, LIKE_POST_SUCCESS,
    POST_CREATE_ERROR,
    POST_CREATE_REQUEST, POST_CREATE_SUCCESS,
    POST_DELETE_ERROR,
    POST_DELETE_REQUEST, POST_DELETE_SUCCESS, UPDATE_POST_ERROR, UPDATE_POST_REQUEST, UPDATE_POST_SUCCESS
} from "./ActionType.js";

export const createPost = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, POST_CREATE_REQUEST, POST_CREATE_SUCCESS, POST_CREATE_ERROR, '/posts/create', {
        method: 'POST',
        body: formData,
        credentials: 'include',
    });
}

export const updatePost = (formData) => async (dispatch) => {
    await dispatchAction(dispatch, UPDATE_POST_REQUEST, UPDATE_POST_SUCCESS, UPDATE_POST_ERROR, `/posts/update/${formData.get("postId")}`, {
        method: 'PUT',
        body: formData,
        credentials: 'include',
    });
}

/*export const getAllPosts = ({ minLikes, maxLikes, sortDir, page, size } = {}) => async (dispatch) => {
    let query = [];
    if (minLikes != null) query.push(`minLikes=${minLikes}`);
    if (maxLikes != null) query.push(`maxLikes=${maxLikes}`);
    if (sortDir) query.push(`sortDir=${sortDir}`);
    if (page != null) query.push(`page=${page}`);
    if (size != null) query.push(`size=${size}`);
    const queryString = query.length > 0 ? '?' + query.join('&') : '';
    
    await dispatchAction(
        dispatch,
        GET_ALL_POSTS_REQUEST,
        GET_ALL_POSTS_SUCCESS,
        GET_ALL_POSTS_ERROR,
        '/posts/all' + queryString,
        { method: 'GET', credentials: 'include' }
    );
};*/

export const getPostsByUser = (nickName) => async (dispatch) => {
    await dispatchAction(dispatch, GET_POSTS_BY_USERID_REQUEST, GET_POSTS_BY_USERID_SUCCESS, GET_POSTS_BY_USERID_ERROR, `/posts/${nickName}/all`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const likePost = (postId) => async (dispatch) => {
    await dispatchAction(dispatch, LIKE_POST_REQUEST, LIKE_POST_SUCCESS, LIKE_POST_ERROR, `/post/${postId}/like`, {
        method: 'PUT',
        credentials: 'include',
    });
}

export const deletePost = (postId) => async (dispatch) => {
    await dispatchAction(dispatch, POST_DELETE_REQUEST, POST_DELETE_SUCCESS, POST_DELETE_ERROR, `/posts/delete/${postId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
};
