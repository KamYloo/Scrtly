import {dispatchAction} from "../../config/api.js";
import {
    CREATE_COMMENT_FAIL,
    CREATE_COMMENT_REQUEST, CREATE_COMMENT_SUCCESS, DELETE_COMMENT_FAIL, DELETE_COMMENT_REQUEST, DELETE_COMMENT_SUCCESS,
    GET_POST_COMMENT_REQUEST,
    GET_POST_COMMENTS_FAIL, GET_POST_COMMENTS_SUCCESS, Like_COMMENT_FAIL, Like_COMMENT_REQUEST, Like_COMMENT_SUCCESS,
} from "./ActionType.js";

export const createComment = (data) => async (dispatch) => {
    await dispatchAction(dispatch, CREATE_COMMENT_REQUEST, CREATE_COMMENT_SUCCESS, CREATE_COMMENT_FAIL, `/api/comments/create`, {
        method: 'POST',
        body: JSON.stringify(data),
        credentials: 'include',
    });
}

export const getAllPostComments = (postId) => async (dispatch) => {
    await dispatchAction(dispatch, GET_POST_COMMENT_REQUEST, GET_POST_COMMENTS_SUCCESS, GET_POST_COMMENTS_FAIL, `/api/comments/all/${postId}`, {
        method: 'GET',
        credentials: 'include',
    });
}

export const likeComment = (commentId) => async (dispatch) => {
    await dispatchAction(dispatch, Like_COMMENT_REQUEST, Like_COMMENT_SUCCESS, Like_COMMENT_FAIL, `/api/comment/${commentId}/like`, {
        method: 'PUT',
        credentials: 'include',
    });
}

export const deleteComment = (commentId) => async (dispatch) => {
    await dispatchAction(dispatch, DELETE_COMMENT_REQUEST, DELETE_COMMENT_SUCCESS, DELETE_COMMENT_FAIL, `/api/comments/delete/${commentId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
};