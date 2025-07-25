import { apiSlice } from '../apiSlice.js';

export const chatApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        getUserChats: builder.query({
            query: () => '/chats/user',
            providesTags: (result) =>
                result
                    ? [
                        ...result.map(chat => ({ type: 'Chat', id: chat.id })),
                        { type: 'Chat', id: 'LIST' },
                    ]
                    : [{ type: 'Chat', id: 'LIST' }],
        }),

        createChat: builder.mutation({
            query: ({ userIds, chatRoomName }) => ({
                url: '/chats/create',
                method: 'POST',
                body: { userIds, chatRoomName },
            }),
            async onQueryStarted(arg, { dispatch, queryFulfilled }) {
                const patchResult = dispatch(
                    chatApi.util.updateQueryData(
                        'getUserChats',
                        undefined,
                        draft => {
                            draft.unshift({ id: 'temp-id', participants: arg.userIds });
                        }
                    )
                );
                try {
                    const { data: newChat } = await queryFulfilled;
                    dispatch(
                        chatApi.util.updateQueryData(
                            'getUserChats',
                            undefined,
                            draft => {
                                const idx = draft.findIndex(c => c.id === 'temp-id');
                                if (idx !== -1) {
                                    draft[idx] = newChat;
                                } else {
                                    draft.unshift(newChat);
                                }
                            }
                        )
                    );
                } catch {
                    patchResult.undo();
                }
            },
        }),

        deleteChat: builder.mutation({
            query: chatRoomId => ({
                url: `/chats/delete/${chatRoomId}`,
                method: 'DELETE',
            }),
            async onQueryStarted(chatRoomId, { dispatch, queryFulfilled }) {
                const patchResult = dispatch(
                    chatApi.util.updateQueryData('getUserChats', undefined, draft => {
                        const idx = draft.findIndex(c => c.id === chatRoomId);
                        if (idx !== -1) draft.splice(idx, 1);
                    })
                );
                try {
                    await queryFulfilled;
                } catch {
                    patchResult.undo();
                }
            },
        }),
    }),
    overrideExisting: false,
});

export const {
    useGetUserChatsQuery,
    useCreateChatMutation,
    useDeleteChatMutation,
} = chatApi;
