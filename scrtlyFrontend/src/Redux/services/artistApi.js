import {apiSlice} from "../apiSlice.js";

export const artistApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        findArtistById: builder.query({
            query: artistId => `/artist/${artistId}`,
            providesTags: (res, err, id) => res ? [{ type: "Artist", id }] : [],
        }),

        getAllArtists: builder.query({
            query: ({ page = 0, size = 9 } = {}) =>
                `/artist/all?page=${page}&size=${size}`,
            transformResponse: (response) => response.content,
            providesTags: (artists = [], err, { page, size }) =>
                artists.map(a => ({ type: "Artist", id: a.id }))
                    .concat([{ type: "Artist", id: "LIST" }]),
        }),

        updateArtist: builder.mutation({
            query: formData => ({
                url: "/artist/update",
                method: "PUT",
                body: formData,
            }),
            invalidatesTags: res => res ? [{ type: "Artist", id: res.id }] : [],
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

        getArtistFans: builder.query({
            query: ({ artistId, page = 0, size = 9 }) =>
                `/artist/${artistId}/fans?page=${page}&size=${size}`,
        }),

    }),
    overrideExisting: false,
});

export const {
    useFindArtistByIdQuery,
    useGetAllArtistsQuery,
    useUpdateArtistMutation,
    useGetArtistTracksQuery,
    useGetArtistFansQuery,
} = artistApi;