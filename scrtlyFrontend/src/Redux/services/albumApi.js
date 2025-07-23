import {apiSlice} from "../apiSlice.js";

export const albumApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        createAlbum: builder.mutation({
            query: formData => ({
                url: "/album/create",
                method: "POST",
                body: formData,
            }),
            invalidatesTags: ["Album"],
        }),

        getAllAlbums: builder.query({
            query: () => "/album/all",
            providesTags: result =>
                result
                    ? [
                        ...result.content.map(({ id }) => ({ type: "Album", id })),
                        { type: "Album", id: "LIST" },
                    ]
                    : [{ type: "Album", id: "LIST" }],
        }),

        getAlbum: builder.query({
            query: albumId => `/album/${albumId}`,
            providesTags: (result, error, albumId) => [{ type: "Album", id: albumId }],
        }),

        getAlbumTracks: builder.query({
            query: albumId => `/album/${albumId}/tracks`,
            providesTags: (result, error, albumId) =>
                result
                    ? [
                        ...result.map(track => ({ type: "Track", id: track.id })),
                        { type: "Track", id: `ALBUM_${albumId}` },
                    ]
                    : [{ type: "Track", id: `ALBUM_${albumId}` }],
        }),

        getArtistAlbums: builder.query({
            query: artistId => `/album/artist/${artistId}`,
            providesTags: result =>
                result
                    ? [
                        ...result.map(({ id }) => ({ type: "Album", id })),
                        { type: "Album", id: `ARTIST_${result.artistId}` },
                    ]
                    : [{ type: "Album", id: `ARTIST_${artistId}` }],
        }),

        uploadSong: builder.mutation({
            query: ({ formData }) => ({
                url: "/song/upload",
                method: "POST",
                body: formData,
            }),
            invalidatesTags: (result) => {
                if (!result) return [];
                const albumId = result.album.id;
                return [
                    { type: "Track", id: `ALBUM_${albumId}` },
                    { type: "Album", id: albumId },
                ];
            },
        }),

        deleteAlbum: builder.mutation({
            query: albumId => ({
                url: `/album/delete/${albumId}`,
                method: "DELETE",
            }),
            invalidatesTags: (result, error, albumId) => [
                { type: "Album", id: albumId },
                { type: "Album", id: "LIST" },
            ],
        }),
    }),
    overrideExisting: false,
});

export const {
    useCreateAlbumMutation,
    useGetAllAlbumsQuery,
    useGetAlbumQuery,
    useGetAlbumTracksQuery,
    useGetArtistAlbumsQuery,
    useUploadSongMutation,
    useDeleteAlbumMutation,
} = albumApi;