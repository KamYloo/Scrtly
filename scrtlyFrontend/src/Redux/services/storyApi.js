import { apiSlice } from "../apiSlice.js";

export const storyApi = apiSlice.injectEndpoints({
    endpoints: (builder) => ({
        createStory: builder.mutation({
            query: (formData) => ({
                url: "/stories/create",
                method: "POST",
                body: formData,
                credentials: "include",
            }),
            async onQueryStarted(formData, { dispatch, queryFulfilled }) {
                const patchUser = dispatch(
                    apiSlice.util.updateQueryData(
                        'getUserStories',
                        undefined,
                        draft => {
                            draft.unshift({ id: -1, ...Object.fromEntries(formData) });
                        }
                    )
                );
                try {
                    const { data: newStory } = await queryFulfilled;
                    dispatch(
                        apiSlice.util.updateQueryData(
                            'getUserStories',
                            undefined,
                            draft => {
                                const idx = draft.findIndex(s => s.id === -1);
                                if (idx !== -1) draft[idx] = newStory;
                            }
                        )
                    );
                } catch {
                    patchUser.undo();
                }
            }
        }),

        getUserStories: builder.query({
            query: () => ({
                url: "/stories/user",
                method: "GET",
                credentials: "include",
            }),
            providesTags: (result = []) =>
                result.length
                    ? [
                        ...result.map((s) => ({ type: "Story", id: s.id })),
                        { type: "Story", id: "USER_LIST" },
                    ]
                    : [{ type: "Story", id: "USER_LIST" }],
        }),

        getFollowedStories: builder.query({
            query: () => ({
                url: "/stories/followed",
                method: "GET",
                credentials: "include",
            }),
            transformResponse: (response) =>
                Object.entries(response).map(([user, stories]) => ({ user, stories })),
            providesTags: (result) =>
                result
                    ? [
                        ...result.flatMap((group) =>
                            group.stories.map((s) => ({ type: "Story", id: s.id }))
                        ),
                        { type: "Story", id: "FOLLOWED_LIST" },
                    ]
                    : [{ type: "Story", id: "FOLLOWED_LIST" }],
        }),

        deleteStory: builder.mutation({
            query: (storyId) => ({
                url: `/stories/delete/${storyId}`,
                method: "DELETE",
                credentials: "include",
            }),
            async onQueryStarted(storyId, { dispatch, queryFulfilled }) {
                const patchUser = dispatch(
                    apiSlice.util.updateQueryData(
                        'getUserStories',
                        undefined,
                        draft => {
                            return draft.filter(s => s.id !== storyId);
                        }
                    )
                );
                const patchFollowed = dispatch(
                    apiSlice.util.updateQueryData(
                        'getFollowedStories',
                        undefined,
                        draft => {
                            draft.forEach(group => {
                                group.stories = group.stories.filter(s => s.id !== storyId);
                            });
                        }
                    )
                );
                try {
                    await queryFulfilled;
                } catch {
                    patchUser.undo();
                    patchFollowed.undo();
                }
            }
        }),
    }),
    overrideExisting: false,
});

export const {
    useCreateStoryMutation,
    useGetUserStoriesQuery,
    useGetFollowedStoriesQuery,
    useDeleteStoryMutation,
} = storyApi;
