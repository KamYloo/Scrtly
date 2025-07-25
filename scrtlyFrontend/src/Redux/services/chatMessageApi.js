// src/Redux/services/messageApi.js
import { apiSlice } from '../apiSlice.js';

export const chatMessageApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        getMessagesByChat: builder.query({
            query: ({ chatId, page = 0 }) => ({
                url: `/messages/chat/${chatId}?page=${page}`,
                method: 'GET',
            }),
            providesTags: (result, error, { chatId }) =>
                result
                    ? [
                        ...result.content.map(m => ({ type: 'Message', id: m.id })),
                        { type: 'ChatMessages', id: chatId },
                    ]
                    : [{ type: 'ChatMessages', id: chatId }],
        }),
    }),
    overrideExisting: false,
});

export const {
    useGetMessagesByChatQuery,
} = chatMessageApi;
