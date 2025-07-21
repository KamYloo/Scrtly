import {apiSlice} from "../apiSlice.js";

export const userApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        findUser: builder.query({
            query: (nickName) => ({
                url: `/user/profile/${encodeURIComponent(nickName)}`,
                method: 'GET',
            }),
            providesTags: (result, error, nickName) =>
                result ? [{ type: 'User', id: nickName }] : [],
        }),
        followUser: builder.mutation({
            query: (userId) => ({
                url: `/user/follow/${userId}`,
                method: 'PUT',
            }),
            invalidatesTags: (result) =>
                result
                    ? [
                        { type: 'User', id: result.nickName },
                        { type: 'User', id: 'CURRENT' },
                    ]
                    : [],
        }),

        searchUser: builder.query({
            query: ({ keyword }) => ({
                url: `/user/search?name=${encodeURIComponent(keyword)}`,
                method: 'GET',
            }),
            providesTags: (result = []) =>
                result.map(user => ({ type: 'User', id: user.id })).concat({ type: 'User', id: 'LIST' }),
        }),

        updateUser: builder.mutation({
            query: formData => ({
                url: '/user/profile/edit',
                method: 'PUT',
                body: formData,
            }),
            invalidatesTags: (result) => [
                { type: 'User', id: result?.nickName || 'CURRENT' },
            ],
        }),

        verifyArtist: builder.mutation({
            query: artistName => ({
                url: '/user/verify-request',
                method: 'POST',
                body: { requestedArtistName: artistName },
            }),
            invalidatesTags: ['User'],
        }),
    }),
    overrideExisting: false,
});

export const {
    useFindUserQuery,
    useFollowUserMutation,
    useSearchUserQuery,
    useUpdateUserMutation,
    useVerifyArtistMutation,
} = userApi;
