import { apiSlice } from "../apiSlice.js";

export const paymentApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        fetchSubscription: builder.query({
            query: () => ({
                url: '/subscription/me',
                method: 'GET',
            }),
            providesTags: (result) =>
                result ? [{ type: 'Subscription', id: result.id || 'CURRENT' }] : [],
        }),

        subscribe: builder.mutation({
            query: (data) => ({
                url: '/subscription/create',
                method: 'POST',
                body: data,
            }),
            invalidatesTags: [{ type: 'Subscription', id: 'CURRENT' }],
        }),

        billingPortal: builder.mutation({
            query: () => ({
                url: '/billing-portal',
                method: 'POST',
            }),
        }),
    }),
    overrideExisting: false,
});

export const {
    useFetchSubscriptionQuery,
    useSubscribeMutation,
    useBillingPortalMutation,
} = paymentApi;
