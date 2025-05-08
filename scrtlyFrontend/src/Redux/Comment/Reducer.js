
import {
    CREATE_COMMENT_FAIL,
    CREATE_COMMENT_REQUEST, CREATE_COMMENT_SUCCESS, DELETE_COMMENT_FAIL,
    DELETE_COMMENT_REQUEST, DELETE_COMMENT_SUCCESS,
    GET_POST_COMMENT_REQUEST, GET_POST_COMMENTS_FAIL, GET_POST_COMMENTS_SUCCESS, Like_COMMENT_FAIL,
    Like_COMMENT_REQUEST, Like_COMMENT_SUCCESS
} from "./ActionType.js";


const initialValue = {
    loading: false,
    error: null,
    createdComment:null,
    comments: {
        content: [],
        pageNumber: 0,
        pageSize: 10,
        totalElements: 0,
        totalPages: 0,
    },
    deletedComment:null,
    likeComment:null,
}

export const commentReducer = (state = initialValue, { type, payload }) => {
    switch (type) {
        case CREATE_COMMENT_REQUEST:
            return { ...state, loading: true, error: null };
        case CREATE_COMMENT_SUCCESS:
            return {
                ...state,
                loading: false,
                createdComment: payload,
                comments: {
                    ...state.comments,
                    content: [payload, ...state.comments.content],
                    totalElements: state.comments.totalElements + 1
                }
            };
        case CREATE_COMMENT_FAIL:
            return { ...state, loading: false, error: payload };

        case GET_POST_COMMENT_REQUEST:
            return { ...state, loading: true, error: null };
        case GET_POST_COMMENTS_SUCCESS:
            return { ...state, loading: false, comments: payload };
        case GET_POST_COMMENTS_FAIL:
            return { ...state, loading: false, error: payload };

        case DELETE_COMMENT_REQUEST:
            return { ...state, loading: true, error: null };
        case DELETE_COMMENT_SUCCESS:
            return {
                ...state,
                loading: false,
                deletedComment: payload,
                comments: {
                    ...state.comments,
                    content: state.comments.content.filter(c => c.id !== payload),
                    totalElements: state.comments.totalElements - 1
                }
            };
        case DELETE_COMMENT_FAIL:
            return { ...state, loading: false, error: payload };

        case Like_COMMENT_REQUEST:
            return { ...state, loading: true, error: null };
        case Like_COMMENT_SUCCESS:
            return {
                ...state,
                loading: false,
                likeComment: payload,
                comments: {
                    ...state.comments,
                    content: state.comments.content.map(c =>
                        c.id === payload.commentId
                            ? { ...c, likedByUser: payload.likedByUser, likeCount: payload.likeCount }
                            : c
                    )
                }
            };
        case Like_COMMENT_FAIL:
            return { ...state, loading: false, error: payload };

        default:
            return state;
    }
};
