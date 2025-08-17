import { apiSlice } from "../apiSlice.js";

export const recommendationApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        getTopAlbums: builder.query({
            query: ({ window = "day", n = 6 } = {}) =>
                `/recommendations/top-albums?window=${window}&n=${n}`,
            method: "GET",
            providesTags: (result = [], error) =>
                result
                    ? [
                        ...result.map(album => ({ type: "Album", id: album.id })),
                        { type: "Album", id: "LIST" },
                    ]
                    : [{ type: "Album", id: "LIST" }],
        }),

        getTopArtists: builder.query({
            query: ({ window = "day", n = 8 } = {}) =>
                `/recommendations/top-artists?window=${window}&n=${n}`,
            method: "GET",
            providesTags: (result = [], error) =>
                result
                    ? [
                        ...result.map(artist => ({ type: "Artist", id: artist.id })),
                        { type: "Artist", id: "LIST" },
                    ]
                    : [{ type: "Artist", id: "LIST" }],
        }),

        getTopSongs: builder.query({
            query: ({ timeWindow  = "day", n = 6 } = {}) =>
                `/recommendations/top-songs?window=${timeWindow }&n=${n}`,
            method: "GET",
            providesTags: (result = [], error) =>
                result
                    ? [
                        ...result.map(song => ({ type: "Song", id: song.id })),
                        { type: "Song", id: "LIST" },
                    ]
                    : [{ type: "Song", id: "LIST" }],
        }),
    }),

    overrideExisting: false,
});

export const {
    useGetTopAlbumsQuery,
    useGetTopArtistsQuery,
    useGetTopSongsQuery,
} = recommendationApi;
