import {apiSlice} from "../apiSlice.js";

export const authApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        register: builder.mutation({ query: creds => ({ url: '/auth/register', method: 'POST', body: creds }), invalidatesTags: ['Auth'] }),
        login:    builder.mutation({ query: creds => ({ url: '/auth/login',    method: 'POST', body: creds }), invalidatesTags: ['Auth','User'] }),
        logout:   builder.mutation({ query: ()    => ({ url: '/auth/logout',   method: 'POST',  responseHandler: 'text' }), invalidatesTags: ['Auth','User'] }),
        forgotPassword: builder.mutation({ query: email => ({ url: `/auth/forgot-password?email=${encodeURIComponent(email)}`, method: 'POST', responseHandler: 'text' }) }),
        resetPassword: builder.mutation({query: ({ userId, token, passwords }) => ({url: `/auth/change-password/${userId}/${token}`, method: 'POST', body: passwords, responseHandler: 'text'}),}),
        getCurrentUser: builder.query({
            query: () => '/auth/check',
            providesTags: (result) =>
                result ? [{ type: 'User', id: 'CURRENT' }] : [],
        }),

    }),
    overrideExisting: false,
});

export const {
    useRegisterMutation,
    useLoginMutation,
    useLogoutMutation,
    useForgotPasswordMutation,
    useResetPasswordMutation,
    useGetCurrentUserQuery,
} = authApi;