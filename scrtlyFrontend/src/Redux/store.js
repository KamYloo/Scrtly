import {applyMiddleware, combineReducers, legacy_createStore} from "redux"
import {thunk} from "redux-thunk"
import {authReducer} from "./Auth/Reducer.js";
import {chatReducer} from "./Chat/Reducer.js";

const rootReducer = combineReducers({
    auth: authReducer,
    chat: chatReducer,
})

export const store = legacy_createStore(rootReducer, applyMiddleware(thunk))