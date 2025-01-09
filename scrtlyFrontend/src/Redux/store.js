import {applyMiddleware, combineReducers, legacy_createStore} from "redux"
import {thunk} from "redux-thunk"
import {authReducer} from "./AuthService/Reducer.js";
import {chatReducer} from "./Chat/Reducer.js";
import {chatMessageReducer} from "./ChatMessage/Reducer.js";
import {postReducer} from "./Post/Reducer.js"
import {commentReducer} from "./Comment/Reducer.js";
import {storyReducer} from "./Story/Reducer.js";
import {artistReducer} from "./Artist/Reducer.js";
import {albumReducer} from "./Album/Reducer.js";
import {songReducer} from "./Song/Reducer.js";
import {playListReducer} from "./PlayList/Reducer.js";

const rootReducer = combineReducers({
    auth: authReducer,
    artist: artistReducer,
    chat: chatReducer,
    chatMessage: chatMessageReducer,
    post: postReducer,
    comment: commentReducer,
    story : storyReducer,
    album : albumReducer,
    playList: playListReducer,
    song: songReducer,
})

export const store = legacy_createStore(rootReducer, applyMiddleware(thunk))