import {apiSlice} from "../apiSlice.js";

export const postApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        createPost: builder.mutation({
            query: formData => ({
                url: '/posts',
                method: 'POST',
                body: formData,
            }),
            async onQueryStarted(formData, { dispatch, queryFulfilled }) {
                const { data: newPost } = await queryFulfilled;
                dispatch(
                    apiSlice.util.updateQueryData(
                        'getAllPosts',
                        draft => {
                            draft.content.unshift(newPost);
                        }
                    )
                );
            },
            invalidatesTags: [{ type: 'Post', id: 'LIST' }],
        }),

        updatePost: builder.mutation({
            query: formData => ({
                url: `/posts/update/${formData.get('postId')}`,
                method: 'PUT',
                body: formData,
            }),
            invalidatesTags: (result) =>
                result
                    ? [
                        { type: 'Post', id: result.id },
                    ]
                    : [],
        }),

        getAllPosts: builder.query({
            query: ({ minLikes, maxLikes, sortDir, page, size } = {}) => {
                const params = new URLSearchParams();
                if (minLikes != null) params.append('minLikes', minLikes);
                if (maxLikes != null) params.append('maxLikes', maxLikes);
                if (sortDir)       params.append('sortDir', sortDir);
                if (page != null)  params.append('page', page);
                if (size != null)  params.append('size', size);
                const qs = params.toString() ? `?${params.toString()}` : '';
                return { url: `/posts/all${qs}`, method: 'GET' };
            },
            providesTags: (result) =>
                result
                    ? [
                        ...result.content.map(post => ({ type: 'Post', id: post.id })),
                        { type: 'Post', id: 'LIST' },
                    ]
                    : [{ type: 'Post', id: 'LIST' }],
        }),

        getPostsByUser: builder.query({
            query: nickName => ({
                url: `/posts/${encodeURIComponent(nickName)}/all`,
                method: 'GET',
            }),
            providesTags: (result, error, nickName) =>
                result
                    ? [
                        ...result.content.map(post => ({ type: 'Post', id: post.id })),
                        { type: 'Post', id: `USER_${nickName}` },
                    ]
                    : [{ type: 'Post', id: `USER_${nickName}` }],
        }),

        likePost: builder.mutation({
            query: postId => ({
                url: `/post/${postId}/like`,
                method: 'PUT',
            }),
            invalidatesTags: (result, error, postId) =>
                result
                    ? [
                        { type: 'Post', id: postId },
                    ]
                    : [],
        }),

        deletePost: builder.mutation({
            query: postId => ({
                url: `/posts/delete/${postId}`,
                method: 'DELETE',
            }),
            invalidatesTags: (result, error, postId) =>
                result
                    ? [
                        { type: 'Post', id: postId },
                    ]
                    : [],
        }),
    }),
    overrideExisting: false,
});

export const {
    useCreatePostMutation,
    useUpdatePostMutation,
    useGetAllPostsQuery,
    useGetPostsByUserQuery,
    useLikePostMutation,
    useDeletePostMutation,
} = postApi;
