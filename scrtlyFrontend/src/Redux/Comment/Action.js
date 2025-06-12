import {dispatchAction} from "../api.js";
import {
    CREATE_COMMENT_FAIL,
    CREATE_COMMENT_REQUEST,
    CREATE_COMMENT_SUCCESS,
    DELETE_COMMENT_FAIL,
    DELETE_COMMENT_REQUEST,
    DELETE_COMMENT_SUCCESS,
    GET_POST_COMMENT_REQUEST,
    GET_POST_COMMENTS_FAIL,
    GET_POST_COMMENTS_SUCCESS, GET_REPLIES_FAIL,
    GET_REPLIES_REQUEST, GET_REPLIES_SUCCESS,
    Like_COMMENT_FAIL,
    Like_COMMENT_REQUEST,
    Like_COMMENT_SUCCESS,
} from "./ActionType.js";

export const createComment = (data) => async (dispatch) => {
    await dispatchAction(dispatch, CREATE_COMMENT_REQUEST, CREATE_COMMENT_SUCCESS, CREATE_COMMENT_FAIL, `/comments/create`, {
        method: 'POST',
        body: JSON.stringify(data),
        credentials: 'include',
    });
}

export const getAllPostComments = (postId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_POST_COMMENT_REQUEST, GET_POST_COMMENTS_SUCCESS, GET_POST_COMMENTS_FAIL, `/comments/all/${postId}`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const likeComment = (commentId) => async (dispatch) => {
    await dispatchAction(dispatch, Like_COMMENT_REQUEST, Like_COMMENT_SUCCESS, Like_COMMENT_FAIL, `/comment/${commentId}/like`, {
        method: 'PUT',
        credentials: 'include',
    });
}

export const deleteComment = (commentId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_COMMENT_REQUEST, DELETE_COMMENT_SUCCESS, DELETE_COMMENT_FAIL, `/comments/delete/${commentId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
};

export const getReplies = (parentCommentId, page = 0, size = 10) => async (dispatch) => {
    return await dispatchAction(dispatch, GET_REPLIES_REQUEST, GET_REPLIES_SUCCESS, GET_REPLIES_FAIL,
        `/comments/replies/${parentCommentId}?page=${page}&size=${size}`, {
            method: 'GET',
            credentials: 'include',
        });
}