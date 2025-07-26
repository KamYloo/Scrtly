import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

const baseQuery = fetchBaseQuery({
    baseUrl: import.meta.env.VITE_APP_BACKEND_URL,
    credentials: 'include',
});

const baseQueryWithReauth = async (args, api, options) => {
    let result = await baseQuery(args, api, options);

    if (result.error?.status === 401) {
        const refreshResult = await baseQuery(
            { url: '/auth/refresh', method: 'POST', responseHandler: 'text' },
            api,
            options
        );

        if (refreshResult.data) {
            result = await baseQuery(args, api, options);
        } else {
            localStorage.removeItem('isLoggedIn');
            window.location.href = '/login';
        }
    }

    return result;
};

export const apiSlice  = createApi({
    reducerPath: 'api',
    baseQuery: baseQueryWithReauth,
    tagTypes: [
        'Album', 'Track', 'Artist', 'Song', 'User', 'Auth',
        "Post", "Comment", "Chat", "ChatMessages", "Subscription",
        "Notifications"],
    endpoints: () => ({}),
});

