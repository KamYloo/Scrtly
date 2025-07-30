import {apiSlice} from "../apiSlice.js";

export const artistApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        findArtistById: builder.query({
            query: artistId => `/artist/${artistId}`,
            providesTags: (result, error, artistId) =>
                result ? [{ type: 'Artist', id: artistId }] : [],
        }),


        getAllArtists: builder.query({
            query: () => '/artist/all',
            providesTags: (result) =>
                result
                    ? [
                        ...result.content.map(({ id }) => ({ type: 'Artist', id })),
                        { type: 'Artist', id: 'LIST' },
                    ]
                    : [{ type: 'Artist', id: 'LIST' }],
        }),

        updateArtist: builder.mutation({
            query: formData => ({
                url: '/artist/update',
                method: 'PUT',
                body: formData,
            }),
            invalidatesTags: (result) =>
                result
                    ? [{ type: 'Artist', id: result.id }]
                    : [],
        }),

        getArtistTracks: builder.query({
            query: ({ artistId, page = 0, size = 9 }) =>
                `/artist/${artistId}/tracks?page=${page}&size=${size}`,
            transformResponse: (response) => response.content,
            providesTags: (tracks = [], error, { artistId }) =>
                tracks.length
                    ? [
                        ...tracks.map(track => ({ type: 'Track', id: track.id })),
                        { type: 'Track', id: `ARTIST_${artistId}` },
                    ]
                    : [{ type: 'Track', id: `ARTIST_${artistId}` }],
        }),

    }),
    overrideExisting: false,
});

export const {
    useFindArtistByIdQuery,
    useGetAllArtistsQuery,
    useUpdateArtistMutation,
    useGetArtistTracksQuery,
} = artistApi;