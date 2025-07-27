import { apiSlice } from "../apiSlice.js";

export const songApi = apiSlice.injectEndpoints({
    endpoints: (builder) => ({
        deleteSong: builder.mutation({
            query: (songId) => ({
                url: `/song/delete/${songId}`,
                method: "DELETE",
            }),
            invalidatesTags: (result, error, songId) => [{ type: "Track", id: songId }],
        }),

        searchSong: builder.query({
            query: (keyword) => ({
                url: `/song/search?title=${encodeURIComponent(keyword)}`,
                method: "GET",
            }),
            keepUnusedDataFor: 30,
            providesTags: (result = []) =>
                result.length
                    ? [
                        ...result.map((song) => ({ type: "SearchResult", id: song.id })),
                        { type: "SearchResult", id: "LIST" },
                    ]
                    : [{ type: "SearchResult", id: "LIST" }],
        }),

        likeSong: builder.mutation({
            query: songId => ({
                url: `/song/${songId}/like`,
                method: "PUT",
                body: JSON.stringify(songId),
            }),
            async onQueryStarted(songId, { dispatch, queryFulfilled }) {
                const patchTop = dispatch(
                    apiSlice.util.updateQueryData(
                        "getTopSongs",
                        { window: "day", n: 6 },
                        draft => {
                            const s = draft.find(x => x.id === songId);
                            if (s) s.favorite = !s.favorite;
                        }
                    )
                );

                try {
                    const { data: likeDto } = await queryFulfilled;
                    const artistId = likeDto.song.artist.id;

                    dispatch(
                        apiSlice.util.updateQueryData(
                            "getArtistTracks",
                            artistId,
                            draft => {
                                const t = draft.content.find(x => x.id === songId);
                                if (t) t.favorite = !t.favorite;
                            }
                        )
                    );
                } catch {
                    patchTop.undo();
                }
            },
        }),

        recordPlay: builder.mutation({
            query: (songId) => ({
                url: `/song/${songId}/play`,
                method: "POST",
                credentials: "include",
            }),
            invalidatesTags: (result, error, songId) => [{ type: "Song", id: songId }],
        }),
    }),
    overrideExisting: false,
});

export const {
    useDeleteSongMutation,
    useSearchSongQuery,
    useLazySearchSongQuery,
    useLikeSongMutation,
    useRecordPlayMutation,
} = songApi;
