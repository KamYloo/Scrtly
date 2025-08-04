import { configureStore } from '@reduxjs/toolkit';
import {apiSlice} from "./apiSlice.js";

const isProduction = import.meta.env.MODE === 'production';

export const store = configureStore({
    reducer: {
        [apiSlice.reducerPath]: apiSlice.reducer,
    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware()
            .concat(apiSlice.middleware),
    devTools: !isProduction,
});
