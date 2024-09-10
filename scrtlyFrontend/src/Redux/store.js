import {applyMiddleware, combineReducers, legacy_createStore} from "redux"
import {thunk} from "redux-thunk"
import {authReducer} from "./Auth/Reducer.js";
import {chatReducer} from "./Chat/Reducer.js";
import {chatMessageReducer} from "./ChatMessage/Reducer.js";
import {postReducer} from "./Post/Reducer.js"
import {commentReducer} from "./Comment/Reducer.js";
import {storyReducer} from "./Story/Reducer.js";

const rootReducer = combineReducers({
    auth: authReducer,
    chat: chatReducer,
    chatMessage: chatMessageReducer,
    post: postReducer,
    comment: commentReducer,
    story : storyReducer,
})

export const store = legacy_createStore(rootReducer, applyMiddleware(thunk))