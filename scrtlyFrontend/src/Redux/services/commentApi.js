import { apiSlice } from '../apiSlice.js';

export const commentApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        createComment: builder.mutation({
            query: data => ({
                url: '/comments/create',
                method: 'POST',
                body: data,
            }),
            async onQueryStarted(arg, { dispatch, queryFulfilled }) {
                try {
                    const { data: newComment } = await queryFulfilled;
                    if (newComment.parentCommentId) {
                        dispatch(
                            commentApi.util.updateQueryData(
                                'getReplies',
                                { parentCommentId: newComment.parentCommentId, page: 0, size: 20 },
                                draft => {
                                    draft.content.unshift(newComment);
                                    draft.totalElements += 1;
                                }
                            )
                        );
                    } else {
                        dispatch(
                            commentApi.util.updateQueryData(
                                'getCommentsByPost',
                                { postId: newComment.post.id, page: 0, size: 20, sortBy: 'all' },
                                draft => {
                                    draft.content.push(newComment);
                                    draft.totalElements += 1;
                                }
                            )
                        );
                    }
                } catch {
                }
            },
        }),

        getCommentsByPost: builder.query({
            query: ({ postId, page = 0, size = 20 }) =>
                `/comments/all/${postId}?page=${page}&size=${size}&sortBy=all`,

            transformResponse: (response, meta, arg) => {
                const filteredContent = response.content.filter(c => !c.parentCommentId);
                return {
                    ...response,
                    content: filteredContent,
                    totalElements: filteredContent.length,
                };
            },

            serializeQueryArgs: ({ endpointName, queryArgs }) =>
                `${endpointName}-${queryArgs.postId}`,

            merge: (currentCache, newCache) => {
                const map = new Map(currentCache.content.map(c => [c.id, c]));
                newCache.content.forEach(comment => {
                    const existing = map.get(comment.id);
                    if (existing) {
                        Object.assign(existing, comment);
                    } else {
                        currentCache.content.push(comment);
                    }
                });
                currentCache.totalPages = newCache.totalPages;
            },

            forceRefetchOnMountOrArgChange: true,

            providesTags: (result, error, { postId }) =>
                result
                    ? [
                        ...result.content.map(c => ({ type: 'Comment', id: c.id })),
                        { type: 'Comment', id: `POST_${postId}` },
                    ]
                    : [{ type: 'Comment', id: `POST_${postId}` }],
        }),



        likeComment: builder.mutation({
            query: ({ commentId }) => ({
                url: `/comment/${commentId}/like`,
                method: 'PUT',
            }),
            async onQueryStarted(
                { commentId, postId, parentCommentId },
                { dispatch, queryFulfilled }
            ) {
                const patchPost = dispatch(
                    commentApi.util.updateQueryData(
                        'getCommentsByPost',
                        { postId, page: 0, size: 20, sortBy: 'all' },
                        draft => {
                            const c = draft.content.find(c => c.id === commentId);
                            if (c) {
                                c.likedByUser = !c.likedByUser;
                                c.likeCount += c.likedByUser ? 1 : -1;
                            }
                        }
                    )
                );

                let patchReplies;
                if (parentCommentId) {
                    patchReplies = dispatch(
                        commentApi.util.updateQueryData(
                            'getReplies',
                            { parentCommentId, page: 0, size: 20 },
                            draft => {
                                const r = draft.content.find(r => r.id === commentId);
                                if (r) {
                                    r.likedByUser = !r.likedByUser;
                                    r.likeCount += r.likedByUser ? 1 : -1;
                                }
                            }
                        )
                    );
                }

                try {
                    await queryFulfilled;
                } catch {
                    patchPost.undo();
                    if (patchReplies) patchReplies.undo();
                }
            },
        }),


        deleteComment: builder.mutation({
            query: ({ commentId }) => ({ url: `/comments/delete/${commentId}`, method: 'DELETE' }),
            async onQueryStarted({ commentId, postId, parentCommentId }, { dispatch, queryFulfilled }) {
                const postPatch = dispatch(
                    commentApi.util.updateQueryData(
                        'getCommentsByPost',
                        { postId, page: 0, size: 20, sortBy: 'all' },
                        draft => {
                            const idx = draft.content.findIndex(c => c.id === commentId);
                            if (idx !== -1) {
                                draft.content.splice(idx, 1);
                                draft.totalElements -= 1;
                            }
                        }
                    )
                );
                let repliesPatch;
                if (parentCommentId) {
                    repliesPatch = dispatch(
                        commentApi.util.updateQueryData(
                            'getReplies',
                            { parentCommentId, page: 0, size: 20 },
                            draft => {
                                const idx = draft.content.findIndex(r => r.id === commentId);
                                if (idx !== -1) {
                                    draft.content.splice(idx, 1);
                                    draft.totalElements -= 1;
                                }
                            }
                        )
                    );
                }

                try {
                    await queryFulfilled;
                } catch {
                    postPatch.undo();
                    if (repliesPatch) repliesPatch.undo();
                }
            },
        }),

        getReplies: builder.query({
            query: ({ parentCommentId, page = 0, size = 20 }) =>
                `/comments/replies/${parentCommentId}?page=${page}&size=${size}`,

            serializeQueryArgs: ({ endpointName, queryArgs }) =>
                `${endpointName}-${queryArgs.parentCommentId}`,

            merge: (currentCache, newCache) => {
                const map = new Map(currentCache.content.map(r => [r.id, r]));
                newCache.content.forEach(reply => {
                    const existing = map.get(reply.id);
                    if (existing) {
                        Object.assign(existing, reply);
                    } else {
                        currentCache.content.push(reply);
                    }
                });
                currentCache.totalPages = newCache.totalPages;
            },

            forceRefetchOnMountOrArgChange: true,

            providesTags: (result, error, { parentCommentId }) =>
                result
                    ? [
                        ...result.content.map(r => ({ type: 'Comment', id: r.id })),
                        { type: 'Comment', id: `REPLIES_${parentCommentId}` },
                    ]
                    : [{ type: 'Comment', id: `REPLIES_${parentCommentId}` }],
        }),
    }),
    overrideExisting: false,
});

export const {
    useCreateCommentMutation,
    useGetCommentsByPostQuery,
    useLazyGetCommentsByPostQuery,
    useLikeCommentMutation,
    useDeleteCommentMutation,
    useGetRepliesQuery,
    useLazyGetRepliesQuery
} = commentApi;
