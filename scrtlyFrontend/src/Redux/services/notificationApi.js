import { apiSlice } from "../apiSlice.js";

export const notificationApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        getNotifications: builder.query({
            query: ({ page = 0, size = 10 } = {}) => ({
                url: `/notifications/own?page=${page}&size=${size}`,
                method: "GET",
                credentials: "include",
            }),
            transformResponse: (response, meta, arg) => ({
                notifications: response.content,
                page: arg.page,
                last: response.last,
            }),
            keepUnusedDataFor: 60,
            providesTags: result =>
                result
                    ? [
                        { type: "Notifications", id: "LIST" },
                        ...result.notifications.map((n) => ({ type: "Notifications", id: n.id })),
                    ]
                    : [{ type: "Notifications", id: "LIST" }],
        }),

        deleteNotification: builder.mutation({
            query: (id) => ({
                url: `/notifications/delete/${id}`,
                method: 'DELETE',
                credentials: 'include',
            }),
            async onQueryStarted(id, { dispatch, queryFulfilled }) {
                const allQueries = dispatch(
                    apiSlice.util.getRunningQueriesThunk('getNotifications')
                );
                const patchResults = allQueries.map(({ originalArgs }) =>
                    dispatch(
                        notificationApi.util.updateQueryData(
                            'getNotifications',
                            originalArgs,
                            draft => {
                                draft.notifications = draft.notifications.filter(n => n.id !== id);
                            }
                        )
                    )
                );
                try {
                    await queryFulfilled;
                } catch {
                    patchResults.forEach(patch => patch.undo());
                }
            },
            invalidatesTags: (result, error, id) => [
                { type: 'Notifications', id },
            ],
        }),
    }),
    overrideExisting: false,
});

export const {
    useGetNotificationsQuery,
    useDeleteNotificationMutation,
} = notificationApi;
